//
//  ARAudioViewController.swift
//  anyHouse-iOS
//
//  Created by 余生丶 on 2021/3/24.
//

import UIKit
import SwiftyJSON
import MJRefresh
import ARtcKit
import ARtmKit
import Alamofire

private let reuseIdentifier = "anyHouse_CellID"
private let leftPadding: CGFloat = 33.0
private let itemSpacing: CGFloat = 24.0

class ARAudioViewController: UIViewController {
    @IBOutlet weak var backView: UIView!
    @IBOutlet weak var micButton: UIButton!
    @IBOutlet weak var nameButton: UIButton!
    @IBOutlet weak var listButton: UIButton!
    @IBOutlet weak var audioButton: UIButton!
    @IBOutlet weak var avatarButton: UIButton!
    @IBOutlet weak var collectionView: ARCollectionView!
    @IBOutlet weak var dropConstraint: NSLayoutConstraint!
    
    private var flowLayout: UICollectionViewFlowLayout? = {
        let layout = UICollectionViewFlowLayout.init()
        layout.sectionInset = UIEdgeInsets(top: 0, left: leftPadding, bottom: 0, right: leftPadding)
        layout.scrollDirection = .vertical
        layout.minimumLineSpacing = 24
        layout.minimumInteritemSpacing = itemSpacing
        return layout
    }()
    /** 断线重连状态 **/
    var stateType: ARConnectionStateType?
    var infoModel: ARRoomInfoModel!
    
    var rtcKit: ARtcEngineKit!
    var rtmEngine: ARtmKit!
    var rtmChannel: ARtmChannel?
    /** 嘉宾、观众 **/
    var modelArr = [[ARMicModel](), [ARMicModel]()]
    
    override func viewDidLoad() {
        super.viewDidLoad()

        // Do any additional setup after loading the view.
        initializeUI()
        getSpeakerList()
        getListenerList()
        initializeEngine()
        joinChannel()
    }
    
    func initializeUI() {
        navigationController?.interactivePopGestureRecognizer?.isEnabled = false
        UIApplication.shared.isIdleTimerDisabled = true
        backView.layer.maskedCorners = [.layerMinXMinYCorner,.layerMaxXMinYCorner]
        nameButton.setTitle(UserDefaults.string(forKey: .userName), for: .normal)
        let avatar = Int(UserDefaults.string(forKey: .avatar) ?? "1")! - 1
        avatarButton.setImage(UIImage(named: headListArr![avatar] as! String), for: .normal)
        collectionView.collectionViewLayout = flowLayout!
        
        if !infoModel.isBroadcaster {
            listButton.isHidden = true
            audioButton.isHidden = true
            micButton.isHidden = false
            view.viewWithTag(56)?.isHidden = false
        }
        
        let refreshHeader = MJRefreshNormalHeader { [weak self] in
            self?.getListenerList()
        }
        refreshHeader.lastUpdatedTimeLabel?.isHidden = true
        refreshHeader.stateLabel?.isHidden = true
        collectionView.mj_header = refreshHeader
        
        NotificationCenter.default.addObserver(self, selector: #selector(invitationUserMic), name: UIResponder.anyHouseNotificationInvitation, object: nil)
    }
    
    func initializeEngine() {
        // init ARtcEngineKit
        rtcKit = ARtcEngineKit.sharedEngine(withAppId: UserDefaults.string(forKey: .appId)!, delegate: self)
        rtcKit.setAudioProfile(.musicHighQuality, scenario: .gameStreaming)
        
        //开启音频AI降噪
        let dic1: NSDictionary = ["Cmd": "SetAudioAiNoise", "Enable": 1]
        rtcKit.setParameters(getJSONStringFromDictionary(dictionary: dic1))
        
        rtcKit.setChannelProfile(.liveBroadcasting)
        if infoModel!.isBroadcaster {
            rtcKit.setClientRole(.broadcaster)
        }
        rtcKit.enableAudioVolumeIndication(500, smooth: 3, report_vad: true)
        
        //init ARtmKit
        rtmEngine = ARtmKit.init(appId: UserDefaults.string(forKey: .appId)!, delegate: self)
        rtmEngine.login(byToken: infoModel?.rtmToken, user: UserDefaults.string(forKey: .uid) ?? "0") { [weak self](errorCode) in
            self?.rtmChannel = self?.rtmEngine.createChannel(withId: (self?.infoModel?.roomId)!, delegate: self)
            self?.rtmChannel?.join(completion: { (errorCode) in
                let dic: NSDictionary! = ["action": 9, "userName": UserDefaults.string(forKey: .userName) as Any, "avatar": Int(UserDefaults.string(forKey: .avatar) ?? "1")!]
                self?.sendChannelMessage(text: (self?.getJSONStringFromDictionary(dictionary: dic))!)
            })
        }
    }
    
    func joinChannel() {
        let uid = UserDefaults.string(forKey: .uid)
        rtcKit.joinChannel(byToken: infoModel?.rtcToken, channelId: (infoModel?.roomId)!, uid: uid) { (channel, uid, elapsed) in
            print("joinChannel sucess")
        }
    }
    
    func leaveChannel() {
        rtcKit.leaveChannel { (stats) in
            print("leaveChannel")
        }
        
        if infoModel.isBroadcaster {
            let dic: NSDictionary! = ["action": 8]
            sendChannelMessage(text: getJSONStringFromDictionary(dictionary: dic))
        }
        leaveRoom(roomId: infoModel.roomId!, broadcaster: infoModel.isBroadcaster)
        ARtcEngineKit.destroy()
        rtmEngine.destroyChannel(withId: (infoModel?.roomId)!)
        rtmEngine.logout(completion: nil)
        UIApplication.shared.isIdleTimerDisabled = false
    }
    
    @IBAction func didClickAudioButton(_ sender: UIButton) {
        sender.isSelected.toggle()
        switch sender.tag {
        case 51:
            //离开
            if infoModel.isBroadcaster {
                UIAlertController.showAlert(in: self, withTitle: "离开", message: "离开后所有人都将退出此房间", cancelButtonTitle: "取消", destructiveButtonTitle: nil, otherButtonTitles: ["离开"]) { [unowned self] (alertVc, action, index) in
                    if index == 2 {
                        self.leaveChannel()
                        self.popBack()
                    }
                }
            } else {
                leaveChannel()
                popBack()
            }
            break
        case 52:
            //音频
            rtcKit.enableLocalAudio(!sender.isSelected)
            modelArr[0].forEach { (micModel) in
                if micModel.uid == UserDefaults.string(forKey: .uid) {
                    micModel.enableAudio = sender.isSelected ? 0 : 1
                    collectionView.reloadSections(NSIndexSet(index: 0) as IndexSet)
                    return
                }
            }
            break
        case 53:
            //举手
            var dic: NSDictionary!
            if sender.isSelected {
                dic = ["action": 1, "userName": UserDefaults.string(forKey: .userName) as Any, "avatar": Int(UserDefaults.string(forKey: .avatar) ?? "1")!]
                updateUserStatus(roomId: infoModel.roomId!, status: 1, uid: UserDefaults.string(forKey: .uid))
            } else {
                dic = ["action": 7]
                updateUserStatus(roomId: infoModel.roomId!, status: 0, uid: UserDefaults.string(forKey: .uid))
            }
            sendPeerMessage(uid: infoModel.ownerId!, dic: dic)
            break
        case 54:
            //拒绝
            micButton.isSelected = false
            invitationedUserMic(micUp: false)
            sendPeerMessage(uid: infoModel.ownerId, dic: ["action": 3, "userName": UserDefaults.string(forKey: .userName) as Any])
            updateUserStatus(roomId: infoModel.roomId!, status: 0, uid: UserDefaults.string(forKey: .uid))
            break
        case 55:
            //同意
            invitationedUserMic(micUp: false)
            becomBroadcaster(role: .broadcaster)
            sendPeerMessage(uid: infoModel.ownerId, dic: ["action": 4])
            updateUserStatus(roomId: infoModel.roomId!, status: 2, uid: UserDefaults.string(forKey: .uid))
            
            for index in 0..<modelArr[1].count {
                let micModel = modelArr[1][index]
                if micModel.uid == UserDefaults.string(forKey: .uid) {
                    modelArr[1].remove(at: index)
                    modelArr[0].append(micModel)
                    collectionView.reloadData()
                    break
                }
            }
            break
        case 56:
            let manager = NetworkReachabilityManager()
            if manager?.networkReachabilityStatus != .notReachable {
                SVProgressHUD.showSuccess(withStatus: "举报成功！")
            } else {
                SVProgressHUD.showError(withStatus: "举报失败！")
            }
            SVProgressHUD.dismiss(withDelay: 1)
            break
        default:
            break
        }
    }
    
    @objc func sendChannelMessage(text: String) {
        let rtmMessage: ARtmMessage = ARtmMessage.init(text: text)
        let options: ARtmSendMessageOptions = ARtmSendMessageOptions()
        rtmChannel?.send(rtmMessage, sendMessageOptions: options) { (errorCode) in
            print("Send Channel Message errorCode:\(errorCode.rawValue)")
        }
    }
    
    private func sendPeerMessage(uid: String!, dic: NSDictionary!) {
        let message: ARtmMessage = ARtmMessage.init(text: getJSONStringFromDictionary(dictionary: dic))
        rtmEngine.send(message, toPeer: uid, sendMessageOptions: ARtmSendMessageOptions()) { (errorCode) in
            print("sendPeerMessage errorCode:\(errorCode.rawValue)")
        }
    }
    
    func tokenExpire() {
        leaveChannel()
        UIAlertController.showAlert(in: self, withTitle: "提示", message: "体验时间已到", cancelButtonTitle: nil, destructiveButtonTitle: nil, otherButtonTitles: ["确定"]) { [unowned self](alertVc, action, index) in
            self.navigationController?.popToRootViewController(animated: true)
        }
    }
    
    func invitationedUserMic(micUp: Bool) {
        //被邀请
        UIView.animate(withDuration: 0.2) {
            self.dropConstraint.constant = micUp ? 0 : -120
            self.view.layoutIfNeeded()
        }
        var statusBarStyle: UIStatusBarStyle?
        micUp ? (statusBarStyle = .lightContent) : (statusBarStyle = .default)
        UIApplication.shared.setStatusBarStyle(statusBarStyle!, animated: true)
    }
    
    @objc func invitationUserMic(nofi: Notification) {
        //反向邀请 --- 主播
        let uid: String = nofi.userInfo!["uid"] as! String
        sendPeerMessage(uid: uid, dic: ["action": 2])
        self.updateUserStatus(roomId: infoModel.roomId!, status: -1, uid: uid)
    }
    
    func getSpeakerList() {
        //获取嘉宾列表
        let parameters : NSDictionary = ["roomId": infoModel.roomId as Any]
        ARNetWorkHepler.getResponseData("getSpeakerList", parameters: parameters as? [String : AnyObject], headers: true, success: { [weak self](result) in
            if result["code"] == 0 {
                let jsonArr = result["data"].arrayValue
                jsonArr.count > 0 ? self?.modelArr[0].removeAll() : nil
                for json in jsonArr {
                    self?.modelArr[0].append(ARMicModel(jsonData: json))
                }
            }
            self?.collectionView.reloadSections(NSIndexSet(index: 0) as IndexSet)
        }) { (error) in
            
        }
    }
    
    func getListenerList() {
        //获取听众列表
        let parameters : NSDictionary = ["roomId": infoModel.roomId as Any]
        ARNetWorkHepler.getResponseData("getListenerList", parameters: parameters as? [String : AnyObject], headers: true) { [weak self](result) in
            if result["code"] == 0 {
                let jsonArr = result["data"].arrayValue
                jsonArr.count > 0 ? self?.modelArr[1].removeAll() : nil
                for json in jsonArr {
                    self?.modelArr[1].append(ARMicModel(jsonData: json))
                }
            }
            self?.collectionView.reloadSections(NSIndexSet(index: 1) as IndexSet)
            self?.collectionView.mj_header?.endRefreshing()
        } error: { (error) in
            self.collectionView.mj_header?.endRefreshing()
        }
    }
    
    @objc private func getHandsUpList() {
        //获取举手列表
        let parameters : NSDictionary = ["roomId": infoModel.roomId as Any]
        ARNetWorkHepler.getResponseData("getHandsUpList", parameters: parameters as? [String : AnyObject], headers: true, success: { [weak self](result) in
            if result["code"] == 0 {
                let jsonArr = result["data"].arrayValue
                self?.listButton.isSelected = (jsonArr.count > 0) ? true : false
            }
        }) { (error) in
            
        }
    }
    
    func getUserInfo(uid: String, section: NSInteger) {
        //获取用户信息
        let parameters : NSDictionary = ["uid": uid]
        ARNetWorkHepler.getResponseData("getUserInfo", parameters: parameters as? [String : AnyObject], headers: true) { [weak self](result) in
            if result["code"] == 0 {
                self?.modelArr[section].append(ARMicModel(jsonData: result["data"]))
            }
            self?.collectionView.reloadSections(NSIndexSet(index: section) as IndexSet)
        } error: { (error) in

        }
    }
    
    private func becomBroadcaster(role: ARClientRole) {
        rtcKit.setClientRole(role)
        if role == .audience {
            //下麦
            audioButton.isHidden = true
            audioButton.isSelected = false
            micButton.isHidden = false
            micButton.isSelected = false
            rtcKit.enableLocalAudio(true)
            Drop.down("您已成为听众", state: .color(UIColor(hexString: "#4BAB63")), duration: 1)
        } else {
            //上麦
            audioButton.isHidden = false
            micButton.isHidden = true
        }
    }
    
    func getCollectionItemSize(section: NSInteger) -> CGSize {
        let allSpacing = 2 * itemSpacing + CGFloat(section) * itemSpacing
        let length = ARScreenWidth - leftPadding * 2 - allSpacing
        let itemWidth = length / CGFloat(3 + section)
        let itemHeight =  itemWidth + 49
        return CGSize.init(width: itemWidth, height: itemHeight)
    }
    
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        guard let identifier = segue.identifier else {
            return
        }
        
        if identifier == "ARMicViewController",
            let vc = segue.destination as? ARMicViewController {
            vc.roomId = infoModel.roomId
            listButton.isSelected = false
        }
    }
    
    deinit {
        print("deinit")
    }
}

//MARK: - ARtcEngineDelegate

extension ARAudioViewController: ARtcEngineDelegate {
    func rtcEngine(_ engine: ARtcEngineKit, didOccurWarning warningCode: ARWarningCode) {
        //发生警告回调
        print(warningCode.rawValue)
    }
    
    func rtcEngine(_ engine: ARtcEngineKit, didOccurError errorCode: ARErrorCode) {
        //发生错误回调
        print(errorCode.rawValue)
    }
    
    func rtcEngine(_ engine: ARtcEngineKit, tokenPrivilegeWillExpire token: String) {
        //Token 过期回调
        tokenExpire()
    }
    
    func rtcEngine(_ engine: ARtcEngineKit, didJoinedOfUid uid: String, elapsed: Int) {
        //远端用户/主播加入回调
        var exist = false
        modelArr[0].forEach { (micModel) in
            if micModel.uid == uid {
                exist = true
                return
            }
        }
        !exist ? getUserInfo(uid: uid, section: 0) : nil
        for index in 0..<modelArr[1].count {
            let micModel = modelArr[1][index]
            if micModel.uid == uid {
                modelArr[1].remove(at: index)
                collectionView.reloadSections(NSIndexSet(index: 1) as IndexSet)
                break
            }
        }
    }
    
    func rtcEngine(_ engine: ARtcEngineKit, didOfflineOfUid uid: String, reason: ARUserOfflineReason) {
        //远端用户（通信场景）/主播（互动场景）离开当前频道回调
        for index in 0..<modelArr[0].count {
            let micModel = modelArr[0][index]
            if micModel.uid == uid {
                modelArr[0].remove(at: index)
                (reason == .becomeAudience) ? modelArr[1].append(micModel) : nil
                collectionView.reloadData()
                break
            }
        }
    }
    
    func rtcEngine(_ engine: ARtcEngineKit, remoteAudioStateChangedOfUid uid: String, state: ARAudioRemoteState, reason: ARAudioRemoteStateReason, elapsed: Int) {
        //远端音频流状态发生改变回调
        if reason == .reasonRemoteMuted || reason == .reasonRemoteUnmuted {
            modelArr[0].forEach { (micModel) in
                if micModel.uid == uid {
                    micModel.enableAudio = (reason == .reasonRemoteMuted) ? 0 : 1
                    collectionView.reloadSections(NSIndexSet(index: 0) as IndexSet)
                    return
                }
            }
        }
    }
    
    func rtcEngine(_ engine: ARtcEngineKit, reportAudioVolumeIndicationOfSpeakers speakers: [ARtcAudioVolumeInfo], totalVolume: Int) {
        for speakInfo in speakers {
            if speakInfo.volume > 3 {
                for index in 0..<modelArr[0].count {
                    let micModel = modelArr[0][index]
                    if speakInfo.uid == micModel.uid || (speakInfo.uid == "0" && micModel.uid == UserDefaults.string(forKey: .uid)){
                        let indexPath: NSIndexPath = NSIndexPath(row: index, section: 0)
                        let cell: ARAudioViewCell? = collectionView.cellForItem(at: indexPath as IndexPath) as? ARAudioViewCell
                        cell?.startAnimation()
                        break
                    }
                }
            }
        }
    }
    
    func rtcEngine(_ engine: ARtcEngineKit, connectionChangedTo state: ARConnectionStateType, reason: ARConnectionChangedReason) {
        if state == .disconnected {
            SVProgressHUD.show(withStatus: "网络断开")
            stateType = state
        } else if state == .connected {
            if stateType == .disconnected {
                getListenerList()
                SVProgressHUD.showSuccess(withStatus: "连接成功")
                SVProgressHUD.dismiss(withDelay: 1.0)
            }
            stateType = state
        }
    }
}

//MARK: - ARtmDelegate, ARtmChannelDelegate

extension ARAudioViewController: ARtmDelegate, ARtmChannelDelegate {
    func rtmKit(_ kit: ARtmKit, messageReceived message: ARtmMessage, fromPeer peerId: String) {
        //收到点对点消息回调
        let dic = getDictionaryFromJSONString(jsonString: message.text)
        let value: NSInteger? = dic.object(forKey: "action") as? NSInteger
        if value == 1 || value == 7 {
            //1举手 7取消举手
            if topViewController() is ARMicViewController {
                NotificationCenter.default.post(name: UIResponder.anyHouseNotificationRefreshHandsUpList, object: self, userInfo:nil)
            }
            value == 1 ? listButton.isSelected = true : getHandsUpList()
        } else if value == 2 {
            //邀请听众上台
            invitationedUserMic(micUp: true)
        } else if value == 3 {
            //拒绝邀请
            Drop.down("\(dic.object(forKey: "userName") ?? "")拒绝邀请为嘉宾", state: .color(UIColor(hexString: "#FF3A30")), duration: 1)
        } else if value == 4 {
            //同意邀请
            for index in 0..<modelArr[1].count {
                let micModel = modelArr[1][index]
                if micModel.uid == peerId {
                    modelArr[1].remove(at: index)
                    collectionView.reloadSections(NSIndexSet(index: 1) as IndexSet)
                    break
                }
            }
        } else if value == 5 {
            //主持人关闭该发言者的麦克风
            rtcKit.enableLocalAudio(false)
            audioButton.isSelected = true
            modelArr[0].forEach { (micModel) in
                if micModel.uid == UserDefaults.string(forKey: .uid) {
                    micModel.enableAudio = 0
                    collectionView.reloadSections(NSIndexSet(index: 0) as IndexSet)
                    return
                }
            }
        } else if value == 6 {
            //主持人将该发言者设置为听众
            becomBroadcaster(role: .audience)
        }
    }
    
    func channel(_ channel: ARtmChannel, messageReceived message: ARtmMessage, from member: ARtmMember) {
        //收到频道消息回调
        let dic = getDictionaryFromJSONString(jsonString: message.text)
        let value: NSInteger? = dic.object(forKey: "action") as? NSInteger
        if value == 8 {
            SVProgressHUD.show(withStatus: "主持人离开房间")
            SVProgressHUD.dismiss(withDelay: 0.8)
            leaveChannel()
            popBack()
        } else if value == 9 {
            var exist = false
            modelArr[1].forEach { (model) in
                if model.uid == member.uid {
                    exist = true
                    return
                }
            }
            
            if !exist {
                let micModel = ARMicModel(jsonData: JSON(parseJSON: message.text))
                micModel.uid = member.uid
                modelArr[1].append(micModel)
                collectionView.reloadSections(NSIndexSet(index: 1) as IndexSet)
            }
        }
    }
    
    func channel(_ channel: ARtmChannel, memberLeft member: ARtmMember) {
        //频道成员离开频道回调
        for index in 0..<modelArr[1].count {
            let micModel = modelArr[1][index]
            if micModel.uid == member.uid {
                modelArr[1].remove(at: index)
                collectionView.reloadSections(NSIndexSet(index: 1) as IndexSet)
                break
            }
        }
    }
}

//MARK: - UICollectionViewDelegate

extension ARAudioViewController: UICollectionViewDelegate, UICollectionViewDataSource, UICollectionViewDelegateFlowLayout {
    func numberOfSections(in collectionView: UICollectionView) -> Int {
        return modelArr.count
    }
    
    func collectionView(_ collectionView: UICollectionView, numberOfItemsInSection section: Int) -> Int {
        return modelArr[section].count
    }
    
    func collectionView(_ collectionView: UICollectionView, cellForItemAt indexPath: IndexPath) -> UICollectionViewCell {
        let collectionViewCell: ARAudioViewCell! = (collectionView.dequeueReusableCell(withReuseIdentifier: reuseIdentifier, for: indexPath) as! ARAudioViewCell)
        // Configure the cell
        collectionViewCell.updateCell(micModel: modelArr[indexPath.section][indexPath.row], width: getCollectionItemSize(section: indexPath.section).width)
        (indexPath.section == 1) ? (collectionViewCell.audioImageView.isHidden = true) : nil
        return collectionViewCell
    }
    
    func collectionView(_ collectionView: UICollectionView, layout collectionViewLayout: UICollectionViewLayout, sizeForItemAt indexPath: IndexPath) -> CGSize {
        return getCollectionItemSize(section: indexPath.section)
    }
    
    func collectionView(_ collectionView: UICollectionView, viewForSupplementaryElementOfKind kind: String, at indexPath: IndexPath) -> UICollectionReusableView {
        let reusableView: ARAudioCollectionReusableView? = collectionView.dequeueReusableSupplementaryView(ofKind: kind, withReuseIdentifier: "anyHouse_ReusableView", for: indexPath) as? ARAudioCollectionReusableView
        reusableView?.update(section: indexPath.section, model: infoModel)
        return reusableView!
    }
    
    func collectionView(_ collectionView: UICollectionView, layout collectionViewLayout: UICollectionViewLayout, referenceSizeForHeaderInSection section: Int) -> CGSize {
        if section == 0 {
            let indexPath = IndexPath(row: 0, section: section)
            let headerView = self.collectionView(collectionView, viewForSupplementaryElementOfKind: UICollectionView.elementKindSectionHeader, at: indexPath)
            let size = headerView.systemLayoutSizeFitting(CGSize(width: collectionView.frame.width, height: UIView.layoutFittingExpandedSize.height), withHorizontalFittingPriority: .required, verticalFittingPriority: .fittingSizeLevel)
            return size
        }
        return CGSize.init(width: ARScreenWidth, height: 50)
    }
    
    
    func collectionView(_ collectionView: UICollectionView, didSelectItemAt indexPath: IndexPath) {
        let micModel: ARMicModel = modelArr[indexPath.section][indexPath.row]
        if indexPath.section == 0 {
            if infoModel.isBroadcaster {
                if micModel.uid != UserDefaults.string(forKey: .uid) {
                    let arr: NSMutableArray = ["关闭麦克风","设置为听众"]
                    (micModel.enableAudio == 0) ? (arr.removeObject(at: 0)) : nil
                    UIAlertController.showActionSheet(in: self, withTitle: nil, message: nil, cancelButtonTitle: "取消", destructiveButtonTitle: nil, otherButtonTitles: arr as? [Any], popoverPresentationControllerBlock: nil) { [unowned self](alertVc, action, index) in
                        if arr.count == 2 && index == 2 {
                            //关闭麦克风
                            self.sendPeerMessage(uid: micModel.uid, dic: ["action": 5])
                        } else if (arr.count == 2 && index == 3) || (arr.count == 1 && index == 2){
                            //设置为观众
                            self.sendPeerMessage(uid: micModel.uid, dic: ["action": 6])
                            self.updateUserStatus(roomId: self.infoModel.roomId!, status: 0, uid: micModel.uid)
                        }
                    }
                }
            } else if micModel.uid == UserDefaults.string(forKey: .uid) {
                UIAlertController.showActionSheet(in: self, withTitle: nil, message: nil, cancelButtonTitle: "取消", destructiveButtonTitle: nil, otherButtonTitles:["下麦"], popoverPresentationControllerBlock: nil) { [unowned self](alertVc, action, index) in
                    if index == 2 {
                        //设置为观众
                        self.becomBroadcaster(role: .audience)
                        
                        for index in 0..<modelArr[0].count {
                            let micModel = modelArr[0][index]
                            if micModel.uid == UserDefaults.string(forKey: .uid) {
                                modelArr[0].remove(at: index)
                                modelArr[1].append(micModel)
                                collectionView.reloadData()
                                break
                            }
                        }
                    }
                }
            }
        } else {
            if infoModel.isBroadcaster {
                UIAlertController.showActionSheet(in: self, withTitle: nil, message: nil, cancelButtonTitle: "取消", destructiveButtonTitle: nil, otherButtonTitles: ["邀请上台发言"], popoverPresentationControllerBlock: nil) { [unowned self](alertVc, action, index) in
                    if index == 2 {
                        self.sendPeerMessage(uid: micModel.uid!, dic: ["action": 2])
                        self.updateUserStatus(roomId: infoModel.roomId!, status: -1, uid: micModel.uid)
                    }
                }
            }
        }
    }
}
