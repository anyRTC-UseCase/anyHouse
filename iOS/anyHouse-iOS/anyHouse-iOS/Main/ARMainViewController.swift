//
//  ARMainViewController.swift
//  anyHouse-iOS
//
//  Created by 余生丶 on 2021/3/22.
//

import UIKit
import MJRefresh

class ARMainViewController: UIViewController {
    @IBOutlet weak var tableView: UITableView!
    @IBOutlet weak var nameButton: UIButton!
    @IBOutlet weak var avatarButton: UIButton!
    
    let identifier = "anyHouse_MainCellID"
    var index = 0
    var modelArr = [ARAudioRoomListModel]()
    lazy var footerView: MJRefreshAutoGifFooter = {
        let footer = MJRefreshAutoGifFooter(refreshingBlock: {
              [weak self] () -> Void in
            guard let weakself = self else {return}
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
        
        tableView.tableFooterView = UIView()
        tableView.separatorStyle = .none
        tableView.rowHeight = UITableView.automaticDimension
        tableView.estimatedRowHeight = 120
        
        tableView.mj_header = MJRefreshNormalHeader(refreshingBlock: {
            [weak self] () -> Void in
            guard let weakself = self else {return}
            weakself.index = 1
            weakself.requestRoomList()
        })
    }
    
    @objc func createPlaceholder() {
        placeholderView.showPlaceholderView(self.tableView, placeholderImageName: "icon_add", placeholderTitle: "可以尝试下拉刷新或者创建房间") {
            self.tableView.mj_header?.beginRefreshing()
        }
        placeholderView.backgroundColor = UIColor.clear
    }
    
    func requestRoomList() {
        //获取房间列表
        let parameters : NSDictionary = ["pageSize": 15, "pageNum": index]
        ARNetWorkHepler.getResponseData("getRoomList", parameters: parameters as? [String : AnyObject], headers: true, success: { [weak self] (result) in
            if result["code"] == 0 {
                (self?.index == 1) ? self?.modelArr.removeAll() : nil
                //自己
                let myList = result["data"]["myList"].arrayValue
                for json in myList {
                    self?.modelArr.append(ARAudioRoomListModel(jsonData: json))
                }
                
                //其他人
                let jsonList = result["data"]["list"].arrayValue
                for json in jsonList {
                    self?.modelArr.append(ARAudioRoomListModel(jsonData: json))
                }
                
                (result["data"]["haveNext"] == 1) ? (self?.tableView.mj_footer = self?.footerView) : (self?.tableView.mj_footer = nil)
                self?.placeholderView.removeFromSuperview()
            } else if (self?.index == 1) {
                self?.modelArr.removeAll()
            }
            self?.endRefresh()
            self?.tableView.reloadData()
        }) { (error) in
            self.endRefresh()
        }
    }
    
    func requestJoinRoom(roomId: String, uType: NSInteger) {
        //加入房间
        SVProgressHUD.show()
        let parameters: NSDictionary = ["roomId": roomId, "cType": 2, "pkg": Bundle.main.infoDictionary!["CFBundleIdentifier"] as Any, "utype": uType]
        ARNetWorkHepler.getResponseData("joinRoom", parameters: parameters as? [String : AnyObject], headers: true) { [weak self](result) in
            if result["code"] == 0 {
                SVProgressHUD.dismiss(withDelay: 0.5)
                let infoModel = ARRoomInfoModel(jsonData: result["data"])
                let storyboard = UIStoryboard.init(name: "Main", bundle: nil)
                guard let audioVc = storyboard.instantiateViewController(withIdentifier: "anyHouse_Audio") as? ARAudioViewController else { return }
                audioVc.infoModel = infoModel
                self?.navigationController?.pushViewController(audioVc, animated: true)
            }
        } error: { (error) in
            SVProgressHUD.dismiss()
        }
    }
    
    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        tableView.mj_header?.beginRefreshing()
        nameButton.setTitle(UserDefaults.string(forKey: .userName), for: .normal)
    }
    
    private func endRefresh() {
        modelArr.count == 0 ? (createPlaceholder()) : nil
        tableView.mj_header?.endRefreshing()
        tableView.mj_footer?.endRefreshing()
    }
}

//MARK: - UITableViewDelegate, UITableViewDataSource

extension ARMainViewController: UITableViewDelegate, UITableViewDataSource {
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return modelArr.count
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: identifier) as? ARMainTableViewCell
        cell?.selectionStyle = .none
        cell?.listModel = modelArr[indexPath.row]
        return cell!
    }
    
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        let listModel = modelArr[indexPath.row]
        if listModel.isPrivate == 1 && !listModel.isBroadcaster {
            let  alertVc =  UIAlertController (title:  "输入房间密码 \n" ,message:  "请输入数字密码" , preferredStyle: .alert)
            alertVc.addTextField { (textField) in
                textField.placeholder = ""
                textField.isSecureTextEntry = true
                textField.keyboardType = .numberPad
            }

            let cancelAction =  UIAlertAction (title:  "取消" , style: .cancel , handler:  nil )
            let okAction =  UIAlertAction (title:  "确认" , style: . default , handler: { [self]
                    action  in
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
            present(alertVc, animated:  true , completion:  nil )
        } else {
            // 1 游客 2 主播
            var uType: NSInteger
            listModel.isBroadcaster ? (uType = 2) : (uType = 1)
            requestJoinRoom(roomId: listModel.roomId!, uType: uType)
        }
    }
}
