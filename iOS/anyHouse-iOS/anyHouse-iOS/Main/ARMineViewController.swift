//
//  ARMineViewController.swift
//  anyHouse-iOS
//
//  Created by 余生丶 on 2021/3/23.
//

import UIKit
import ARtcKit

struct MenuItem {
    var name: String
    var icon: String?
    var detail: String?
}

class ARMineViewController: UIViewController {
    
    @IBOutlet weak var tableView: UITableView!
    
    private let identifier0 = "anyHouse_MineCellID0"
    private let identifier1 = "anyHouse_MineCellID1"
    
    lazy var urlArr = {
        return [
            "https://www.anyrtc.io/hide",
            "https://anyrtc.io/termsOfService",
            "https://console.anyrtc.io/signup"
        ]
    }()
    
    var menus = [
        [MenuItem(name: "头像", icon: headListArr![Int(UserDefaults.string(forKey: .avatar) ?? "1")! - 1] as? String),
         MenuItem(name: "昵称", detail: UserDefaults.string(forKey: .userName))],
        
        [MenuItem(name: "隐私条例", icon: "icon_arrow"),
         MenuItem(name: "免责声明", icon: "icon_arrow"),
         MenuItem(name: "注册开发者账号", icon: "icon_arrow")],
        
        [MenuItem(name: "发版时间", detail: "2021-04-10"),
         MenuItem(name: "SDK版本", detail:  String(format: "v %@", ARtcEngineKit.getSdkVersion())),
         MenuItem(name: "软件版本", detail: String(format: "v %@", Bundle.main.infoDictionary!["CFBundleShortVersionString"] as! CVarArg))]
    ]

    override func viewDidLoad() {
        super.viewDidLoad()

        // Do any additional setup after loading the view.
        tableView.separatorStyle = .singleLine
        tableView.backgroundColor = UIColor(hexString: "#F1EFE5")
        navigationController?.interactivePopGestureRecognizer?.isEnabled = false
    }
    
    @IBAction func didClickPopButton(_ sender: Any) {
        popBack()
    }
    
    @objc func textFieldValueChange(textField: UITextField?) {
        if textField?.text?.count ?? 0 > 8 {
            textField?.text = String((textField?.text?.prefix(8))!)
        }
    }
    
    @objc func saveNickname(nickName: String!) {
        if nickName != UserDefaults.string(forKey: .userName) {
            if stringAllIsEmpty(string: nickName) {
                SVProgressHUD.showError(withStatus: "昵称不能为空")
                SVProgressHUD.dismiss(withDelay: 0.5)
            } else {
                UserDefaults.set(value: nickName! , forKey: .userName)
                //修改昵称
                let parameters : NSDictionary = ["userName": nickName as Any]
                ARNetWorkHepler.getResponseData("updateUserName", parameters: parameters as? [String : AnyObject], headers: true, success: {[weak self](result) in
                    if result["code"] == 0 {
                        self?.menus[0][1].detail = nickName
                        self?.tableView.reloadData()
                        SVProgressHUD.showSuccess(withStatus: "修改昵称成功")
                        SVProgressHUD.dismiss(withDelay: 0.5)
                    }
                }) { (error) in
                    
                }
            }
        }
    }
}

//MARK: - UITableViewDelegate, UITableViewDataSource

extension ARMineViewController: UITableViewDelegate, UITableViewDataSource {
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        var cell: UITableViewCell?
        let menuItem = menus[indexPath.section][indexPath.row]
        if (indexPath.section == 0 && indexPath.row == 0) || indexPath.section == 1 {
            cell = tableView.dequeueReusableCell(withIdentifier: identifier0)
            if cell == nil {
                cell = ARGeneralTableViewCell.init(style: .default, reuseIdentifier: identifier0)
            }
            let imageView = UIImageView.init()
            if indexPath.section == 0 {
                imageView.frame = CGRect.init(x: 0, y: 0, width: 50, height: 50)
                imageView.image = UIImage(named: menuItem.icon!)
                imageView.layer.masksToBounds = true
                imageView.layer.cornerRadius = 22
            } else {
                imageView.frame = CGRect.init(x: 0, y: 0, width: 25, height: 25)
                imageView.image = UIImage(named: "icon_arrow")
            }
            cell?.accessoryView = imageView
        } else {
            cell = tableView.dequeueReusableCell(withIdentifier: identifier1)
            if cell == nil {
                cell = ARGeneralTableViewCell.init(style: .value1, reuseIdentifier: identifier1)
            }
            cell?.detailTextLabel?.text = menuItem.detail
            cell?.detailTextLabel?.textColor = UIColor(hexString: "#050504")
            cell?.detailTextLabel?.font = UIFont.init(name: "PingFang SC", size: 14)
        }
        
        if indexPath.section == 0 && indexPath.row == 1 {
            cell?.accessoryType = .disclosureIndicator
        }
        
        cell?.backgroundColor = UIColor.white
        cell?.textLabel?.text = menuItem.name
        cell?.textLabel?.font = UIFont.init(name: "PingFang SC", size: 14)
        cell?.selectionStyle = .none
        return cell!
    }
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return menus[section].count
    }
    
    func numberOfSections(in tableView: UITableView) -> Int {
        return menus.count
    }
    
    func tableView(_ tableView: UITableView, heightForRowAt indexPath: IndexPath) -> CGFloat {
        return (indexPath.section == 0 && indexPath.row == 0) ? 61 : 45
    }
    
    func tableView(_ tableView: UITableView, heightForHeaderInSection section: Int) -> CGFloat {
        return 0.1
    }
    
    func tableView(_ tableView: UITableView, heightForFooterInSection section: Int) -> CGFloat {
        return 20
    }
    
    func tableView(_ tableView: UITableView, willDisplay cell: UITableViewCell, forRowAt indexPath: IndexPath) {
        cell.addSectionCorner(at: indexPath, radius: 11)
    }
    
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        if indexPath.section == 0 {
            if indexPath.row == 1 {
                //修改昵称
                let  alertVc =  UIAlertController (title:  "修改昵称" ,message:  "最多8个字" , preferredStyle: .alert)
                
                alertVc.addTextField { (textField) in
                    textField.placeholder = "请输入昵称"
                    textField.addTarget(self, action: #selector(self.textFieldValueChange), for: .editingChanged)
                }

                let cancelAction =  UIAlertAction (title:  "取消" , style: .cancel , handler:  nil )
                let okAction =  UIAlertAction (title:  "好的" , style: . default , handler: {
                        action  in
                    let nameTextField = alertVc.textFields?.first
                    if nameTextField?.text?.count ?? 0 > 0 {
                        self.saveNickname(nickName: nameTextField?.text)
                    }
                })
                alertVc.addAction(cancelAction)
                alertVc.addAction(okAction)
                present(alertVc, animated:  true , completion:  nil )
            }
        } else if indexPath.section == 1 {
            UIApplication.shared.open(NSURL(string: urlArr[indexPath.row])! as URL, options: [:], completionHandler: nil)
        }
    }
}

