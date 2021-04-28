//
//  ARCreateRoomViewController.swift
//  anyHouse-iOS
//
//  Created by 余生丶 on 2021/3/22.
//

import UIKit

class ARCreateRoomViewController: UIViewController {
    @IBOutlet weak var backView: UIView!
    @IBOutlet weak var passwordView: UIView!
    @IBOutlet weak var topicLabel: UILabel!
    @IBOutlet weak var publicButton: UIButton!
    @IBOutlet weak var passwordButton: UIButton!
    @IBOutlet weak var passwordTextField: UITextField!
    @IBOutlet weak var padding: NSLayoutConstraint!
    
    var isPrivate = 0
    var topic: String = ""
    let publicText = "创建房间对所有人开放"
    let passwordText = "创建私密房间需要输入密码"
    let tap = UITapGestureRecognizer()
    
    override func viewDidLoad() {
        super.viewDidLoad()

        // Do any additional setup after loading the view.
        backView.layer.maskedCorners = [.layerMinXMinYCorner,.layerMaxXMinYCorner]
        padding.constant = 0
        passwordTextField.addTarget(self, action: #selector(textFieldValueChange), for: .editingChanged)
        tap.delegate = self
        self.view.addGestureRecognizer(tap)
    }
    
    @objc func textFieldValueChange() {
        let password = passwordTextField.text
        if password?.count ?? 0 > 8 {
            passwordTextField.text = String((password?.prefix(8))!)
        }
    }
    
    @IBAction func didClickTopicButton(_ sender: Any) {
        passwordTextField.resignFirstResponder()
        let alertVc = ARAlertTextViewController(title: "添加话题 \n ", message: "比如发生在身边的趣事", preferredStyle: .alert)
        alertVc.updateTextView(text: topic)
        let cancelAction =  UIAlertAction (title:  "取消" , style: .cancel , handler:  nil )
        let okAction =  UIAlertAction (title:  "设置话题" , style: . default , handler: {
                action  in
            if !self.stringAllIsEmpty(string: alertVc.textView.text) {
                self.topic = alertVc.textView.text ?? ""
                self.updateTopic()
            }
        })
        alertVc.addAction(cancelAction)
        alertVc.addAction(okAction)
        present(alertVc, animated: true, completion: nil)
        DispatchQueue.main.asyncAfter(deadline: DispatchTime.now() + 0.1) {
            alertVc.textView.becomeFirstResponder()
        }
    }
    
    func updateTopic() {
        if isPrivate == 0 {
            (topic.count == 0) ? (topicLabel.text = publicText) : (topicLabel.text = String(format: "%@：“%@”", publicText,topic))
        } else {
            (topic.count == 0) ? (topicLabel.text = passwordText) : (topicLabel.text = String(format: "%@：“%@”", passwordText,topic))
        }
    }
    
    @IBAction func didClickButton(_ sender: UIButton) {
        if sender.tag != isPrivate {
            isPrivate = sender.tag
            passwordTextField.resignFirstResponder()
            updateTopic()
            if isPrivate == 0 {
                //公开
                passwordView.isHidden = true
                padding.constant = 0
                publicButton.backgroundColor = UIColor(hexString: "#DFE2EE")
                passwordButton.backgroundColor = UIColor.white
            } else {
                //私密
                passwordView.isHidden = false
                padding.constant = 47
                passwordButton.backgroundColor = UIColor(hexString: "#DFE2EE")
                publicButton.backgroundColor = UIColor.white
            }
        }
    }
    
    @IBAction func createAudioRoom(_ sender: Any) {
        if isPrivate == 1 && passwordTextField.text?.count == 0 {
            SVProgressHUD.showError(withStatus: "请输入房间密码")
            SVProgressHUD.dismiss(withDelay: 0.5)
            return
        }
        
        SVProgressHUD.show(withStatus: "正在创建")
        (topic.count == 0) ? (topic = "\(UserDefaults.string(forKey: .userName) ?? "")的房间") : nil
        let parameters : NSDictionary = ["cType": 2, "pkg": Bundle.main.infoDictionary!["CFBundleIdentifier"] as Any, "isPrivate": isPrivate, "rType": 4, "roomName": topic, "roomPwd": passwordTextField.text as Any, "isMultAdd": 1]
        ARNetWorkHepler.getResponseData("addRoom", parameters: parameters as? [String : AnyObject], headers: true, success: {[weak self] (result) in
            if result["code"] == 0 {
                var infoModel = ARRoomInfoModel(jsonData: result["data"])
                infoModel.ownerId = UserDefaults.string(forKey: .uid)
                infoModel.isBroadcaster = true
                
                let storyboard = UIStoryboard.init(name: "Main", bundle: nil)
                guard let audioVc = storyboard.instantiateViewController(withIdentifier: "anyHouse_Audio") as? ARAudioViewController else { return }
                audioVc.infoModel = infoModel
                self?.dismiss(animated: false, completion: {
                    UIApplication.shared.keyWindow?.rootViewController?.children.last?.navigationController?.pushViewController(audioVc, animated: true)
                })
            }
            SVProgressHUD.dismiss(withDelay: 0.5)
        }) { (error) in
            
        }
    }
}

extension ARCreateRoomViewController: UIGestureRecognizerDelegate {
    func gestureRecognizer(_ gestureRecognizer: UIGestureRecognizer, shouldReceive touch: UITouch) -> Bool {
        if(touch.view == self.view) {
            self.dismiss(animated: false, completion: nil)
            return true
        } else {
            return false
        }
    }
}
