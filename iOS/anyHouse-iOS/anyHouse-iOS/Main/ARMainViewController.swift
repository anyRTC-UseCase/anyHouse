//
//  ARMainViewController.swift
//  anyHouse-iOS
//
//  Created by 余生丶 on 2021/3/22.
//

import MJRefresh
import UIKit

class ARMainViewController: UIViewController {
    @IBOutlet var tableView: UITableView!
    @IBOutlet var nameButton: UIButton!
    @IBOutlet var avatarButton: UIButton!
    
    let identifier = "anyHouse_MainCellID"
    /** 黑名单 **/
    let blacklistIdentifier = "blacklistIdentifier"
    var index = 0
    var modelArr = [ARAudioRoomListModel]()
    var blackList: NSMutableArray = .init()
    
    lazy var footerView: MJRefreshAutoGifFooter = {
        let footer = MJRefreshAutoGifFooter(refreshingBlock: {
            [weak self] () in
                guard let weakself = self else { return }
                weakself.index += 1
                weakself.requestRoomList()
        })
        return footer
    }()
    
    lazy var placeholderView: ARPlaceholderView = {
        let placeholderView = ARPlaceholderView()
        return ARPlaceholderView()
    }()
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        // Do any additional setup after loading the view.
        let avatar = Int(UserDefaults.string(forKey: .avatar) ?? "1")! - 1
        avatarButton.setImage(UIImage(named: headListArr![avatar] as! String), for: .normal)
        
        let arr = UserDefaults.standard.array(forKey: blacklistIdentifier)
        arr?.count ?? 0 > 0 ? (blackList.addObjects(from: arr!)) : nil
        
        tableView.tableFooterView = UIView()
        tableView.separatorStyle = .none
        tableView.rowHeight = UITableView.automaticDimension
        tableView.estimatedRowHeight = 120
        
        tableView.mj_header = MJRefreshNormalHeader(refreshingBlock: {
            [weak self] () in
                guard let weakself = self else { return }
                weakself.index = 1
                weakself.requestRoomList()
        })
    }
    
    @objc func createPlaceholder() {
        placeholderView.showPlaceholderView(tableView, placeholderImageName: "icon_add", placeholderTitle: "可以尝试下拉刷新或者创建房间") {
            self.tableView.mj_header?.beginRefreshing()
        }
        placeholderView.backgroundColor = UIColor.clear
    }
    
    func requestRoomList() {
        // 获取房间列表
        let parameters: NSDictionary = ["pageSize": 15, "pageNum": index]
        ARNetWorkHepler.getResponseData("getRoomList", parameters: parameters as? [String: AnyObject], headers: true, success: { [weak self] result in
            if result["code"] == 0 {
                (self?.index == 1) ? self?.modelArr.removeAll() : nil
                // 自己
                let myList = result["data"]["myList"].arrayValue
                for json in myList {
                    self?.modelArr.append(ARAudioRoomListModel(jsonData: json))
                }
                
                // 其他人
                let jsonList = result["data"]["list"].arrayValue
                for json in jsonList {
                    let listModel = ARAudioRoomListModel(jsonData: json)
                    if self?.blackList.contains(listModel.ownerUid as Any) == false {
                        self?.modelArr.append(listModel)
                    }
                }
                
                (result["data"]["haveNext"] == 1) ? (self?.tableView.mj_footer = self?.footerView) : (self?.tableView.mj_footer = nil)
                self?.placeholderView.removeFromSuperview()
            } else if self?.index == 1 {
                self?.modelArr.removeAll()
            }
            self?.endRefresh()
            self?.tableView.reloadData()
        }) { _ in
            self.endRefresh()
        }
    }
    
    func requestJoinRoom(roomId: String, uType: NSInteger) {
        // 加入房间
        SVProgressHUD.show()
        let parameters: NSDictionary = ["roomId": roomId, "cType": 2, "pkg": Bundle.main.infoDictionary!["CFBundleIdentifier"] as Any, "utype": uType]
        ARNetWorkHepler.getResponseData("joinRoom", parameters: parameters as? [String: AnyObject], headers: true) { [weak self] result in
            if result["code"] == 0 {
                SVProgressHUD.dismiss(withDelay: 0.5)
                let infoModel = ARRoomInfoModel(jsonData: result["data"])
                let storyboard = UIStoryboard(name: "Main", bundle: nil)
                guard let audioVc = storyboard.instantiateViewController(withIdentifier: "anyHouse_Audio") as? ARAudioViewController else { return }
                audioVc.infoModel = infoModel
                self?.navigationController?.pushViewController(audioVc, animated: true)
            }
        } error: { _ in
            SVProgressHUD.dismiss()
        }
    }
    
    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        tableView.mj_header?.beginRefreshing()
        nameButton.setTitle(UserDefaults.string(forKey: .userName), for: .normal)
    }
    
    private func endRefresh() {
        modelArr.count == 0 ? createPlaceholder() : nil
        tableView.mj_header?.endRefreshing()
        tableView.mj_footer?.endRefreshing()
    }
    
    private func setUserBlack(ownerUid: String) {
        // 拉黑
        var arr = UserDefaults.standard.array(forKey: blacklistIdentifier)
        if arr?.count ?? 0 > 0 {
            arr?.append(ownerUid as Any)
        } else {
            arr = [ownerUid as Any]
        }
        UserDefaults.standard.setValue(arr, forKey: blacklistIdentifier)
        
        for index in 0 ..< modelArr.count {
            let micModel = modelArr[index]
            if micModel.ownerUid == ownerUid {
                modelArr.remove(at: index)
                modelArr.count == 0 ? createPlaceholder() : nil
                tableView.reloadData()
                break
            }
        }
        blackList.add(ownerUid)
    }
}

// MARK: - UITableViewDelegate, UITableViewDataSource

extension ARMainViewController: UITableViewDelegate, UITableViewDataSource {
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return modelArr.count
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: identifier) as? ARMainTableViewCell
        cell?.selectionStyle = .none
        let mainModel = modelArr[indexPath.row]
        cell?.listModel = mainModel
        cell?.onButtonTapped = { [weak self] () in
            UIAlertController.showAlert(in: self!, withTitle: "屏蔽", message: "屏蔽该用户", cancelButtonTitle: "取消", destructiveButtonTitle: nil, otherButtonTitles: ["确定"]) { _, _, index in
                if index == 2 {
                    self!.setUserBlack(ownerUid: mainModel.ownerUid!)
                }
            }
        }
        return cell!
    }
    
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        let listModel = modelArr[indexPath.row]
        if listModel.isPrivate == 1, !listModel.isBroadcaster {
            let alertVc = UIAlertController(title: "输入房间密码 \n", message: "请输入数字密码", preferredStyle: .alert)
            alertVc.addTextField { textField in
                textField.placeholder = ""
                textField.isSecureTextEntry = true
                textField.keyboardType = .numberPad
            }

            let cancelAction = UIAlertAction(title: "取消", style: .cancel, handler: nil)
            let okAction = UIAlertAction(title: "确认", style: .default, handler: { [self]
                _ in
                let passwordField = alertVc.textFields?.first
                if passwordField?.text == listModel.roomPwd {
                    self.requestJoinRoom(roomId: listModel.roomId!, uType: 1)
                } else {
                    SVProgressHUD.showError(withStatus: "密码错误")
                    SVProgressHUD.dismiss(withDelay: 0.5)
                }
            })
            alertVc.addAction(cancelAction)
            alertVc.addAction(okAction)
            present(alertVc, animated: true, completion: nil)
        } else {
            // 1 游客 2 主播
            var uType: NSInteger
            listModel.isBroadcaster ? (uType = 2) : (uType = 1)
            requestJoinRoom(roomId: listModel.roomId!, uType: uType)
        }
    }
}
