//
//  ARNetWorkHepler.swift
//  AudioLive-iOS
//
//  Created by 余生丶 on 2021/2/22.
//

import UIKit
import Alamofire
import SwiftyJSON

private let requestUrl = "https://arlive.agrtc.cn/arapi/arlive/v1/anyhouse/"

class ARNetWorkHepler: NSObject {
    class func getResponseData(_ url: String, parameters: [String: AnyObject]? = nil, headers: Bool, success:@escaping(_ result: JSON)-> Void, error:@escaping (_ error: NSError)->Void) {
        UIApplication.shared.isNetworkActivityIndicatorVisible = true
        let resultUrl = requestUrl + url
        let urls = NSURL(string: resultUrl as String)
        var request = URLRequest(url: urls! as URL)
        request.httpMethod = "POST"
        request.setValue("application/json", forHTTPHeaderField:"Content-Type")
        if headers {
            guard let token = UserDefaults.string(forKey: .userToken) else {return}
            request.setValue(token, forHTTPHeaderField: "artc-token")
        }
        
        if parameters != nil {
            let data = try! JSONSerialization.data(withJSONObject: parameters!, options: JSONSerialization.WritingOptions.prettyPrinted)

            let json = NSString(data: data, encoding: String.Encoding.utf8.rawValue)
            request.httpBody = json!.data(using: String.Encoding.utf8.rawValue)
        }
        
        let alamoRequest = Alamofire.request(request as URLRequestConvertible)
        alamoRequest.validate(statusCode: 200..<300)
        alamoRequest.responseString { response in
            if let jsonData = response.result.value {
                success(JSON(parseJSON: jsonData))
            } else if let er = response.result.error {
                error(er as NSError)
                if er.localizedDescription .contains("401") {
                    let storyboard = UIStoryboard.init(name: "Main", bundle: nil)
                    guard let signInVc = storyboard.instantiateViewController(withIdentifier: "anyHouse_SignIn") as? ViewController else { return }
                    UIApplication.shared.keyWindow?.rootViewController = signInVc
                } else if response.result.error.debugDescription.contains("-1020") {
                    SVProgressHUD.showError(withStatus: "网络异常，请检查当前网络状态")
                    SVProgressHUD.dismiss(withDelay: 1)
                }
            }
            UIApplication.shared.isNetworkActivityIndicatorVisible = false
        }
    }
}
