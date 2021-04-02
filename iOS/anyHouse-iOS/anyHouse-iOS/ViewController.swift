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
        if nameTextField.text?.count ?? 0 > 0 {
            nameTextField.resignFirstResponder()
            login(userName: nameTextField.text!)
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
}

