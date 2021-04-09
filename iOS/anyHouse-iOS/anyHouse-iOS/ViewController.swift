//
//  ViewController.swift
//  anyHouse-iOS
//
//  Created by 余生丶 on 2021/3/22.
//

import UIKit
import AttributedString

public var headListArr: NSArray? = {
    var arr = [String]()
    for index in 1...10 {
        arr.append("icon_head\(index)")
    }
    return arr as NSArray
}()

class ViewController: UIViewController {
    @IBOutlet weak var createLabel: UILabel!
    @IBOutlet weak var tipLabel: UILabel!
    @IBOutlet weak var loginButton: UIButton!
    @IBOutlet weak var nameTextField: UITextField!
    var isAgree: Bool = false
    
    override func viewDidLoad() {
        super.viewDidLoad()
        // Do any additional setup after loading the view.
        
        nameTextField.addTarget(self, action: #selector(textFieldValueChange), for: .editingChanged)
        createLabel.attributed.text = .init("""
        \(.image(#imageLiteral(resourceName: "icon_fireworks"), .custom(.center,size: .init(width: 36, height: 36)))) 创建账号
        """)
        
        tipLabel.attributed.text = .init("""
        请在下方输入您的昵称，最长8个字 \(.image(#imageLiteral(resourceName: "icon_smile"), .custom(.center, size: .init(width: 24, height: 24))))
        """)
    }
    
    @IBAction func didClickLoginButton(_ sender: Any) {
        nameTextField.resignFirstResponder()
        if isAgree {
            let nickName: String = nameTextField.text ?? ""
            if !stringAllIsEmpty(string: nickName) {
                login(userName: nameTextField.text!)
            } else {
                SVProgressHUD.showError(withStatus: "昵称不能为空")
                SVProgressHUD.dismiss(withDelay: 0.5)
            }
        } else {
            popUpAgreement()
        }
    }
    
    func popUpAgreement() {
        UIAlertController.showAlert(in: self, withTitle: "协议条款", message: "anyHouse（“本产品”）是由 anyRTC 提供的一款体验产品，主要面向 anyRTC 客户展示内嵌 anyRTC SDK 后可以实现的效果。本产品仅为体验效果之目的提供给现有或潜在客户使用，而非提供给大众使用的正式产品。 anyRTC 享有本产品的著作权和所有权，任何人不得对本产品进行修改、合并、调整、逆向工程、再许可和/或出售该软件的副本以及做出其他损害 anyRTC 合法权益的行为。 若您想试用本产品，欢迎您下载、安装并使用，anyRTC 特此授权您在全球范围内免费使用本产品的权利。本产品按“现状”提供，没有任何形式的明示或暗示担保，包括但不限于对适配性、特定目的的适用性和非侵权性的担保。无论是由于与本产品或本产品的使用或其他方式有关的任何合同、侵权或其他形式的行为，anyRTC 均不对任何索赔、损害或其他责任负责。如果您下载、安装、使用本产品，即表明您确认并同意 anyRTC 对因任何原因在试用本产品时可能对您自身或他人造成的任何形式的损失和伤害不承担任何责任。 若您有任何疑问，请联系 hi@dync.cc.", cancelButtonTitle: "暂不使用", destructiveButtonTitle: nil, otherButtonTitles: ["同意"]) { [unowned self] (alertVc, ation, index) in
            if index == 2 {
                self.isAgree = true
            }
        }
    }
    
    @objc func textFieldValueChange() {
        let nickName = nameTextField.text
        if nickName?.count ?? 0 > 8 {
            nameTextField.text = String((nickName?.prefix(8))!)
        }
        
        loginButton.isEnabled = (nickName?.count != 0)
        (nickName?.count ?? 0 > 0) ? (loginButton.backgroundColor = UIColor(hexString: "#5B75A6")) : (loginButton.backgroundColor = UIColor(hexString: "#999999"))
    }
    
    override func touchesBegan(_ touches: Set<UITouch>, with event: UIEvent?) {
        view.endEditing(true)
    }
    
    override func viewDidAppear(_ animated: Bool) {
        popUpAgreement()
    }
}

