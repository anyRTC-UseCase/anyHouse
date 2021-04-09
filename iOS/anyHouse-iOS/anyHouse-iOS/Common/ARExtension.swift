//
//  ARExtension.swift
//  AR-Voice-Tutorial-iOS
//
//  Created by 余生丶 on 2020/9/2.
//  Copyright © 2020 AR. All rights reserved.
//

import UIKit
import ARtmKit
import SnapKit

let ARScreenHeight = UIScreen.main.bounds.size.height
let ARScreenWidth = UIScreen.main.bounds.size.width

extension NSObject {
    
    //RGBA转换
    func RGBA(r:CGFloat,g:CGFloat,b:CGFloat,a: CGFloat) ->UIColor{
        //
        return UIColor(red: r/225.0, green: g/225.0, blue: b/225.0, alpha: a)
    }
    
    func randomCharacter(length : NSInteger) ->String {
        var randomStr = ""
        for _ in 1 ... length{
            let num = 65 + arc4random()%25 //随机6位大写字母
            let randomCharacter = Character(UnicodeScalar(num)!)
            randomStr.append(randomCharacter)
        }
        return randomStr
    }
    
    func generateRandomNumber(num: Int) -> Int{
       var place = 1
       var finalNumber = 0
        
        for _ in 0..<num {
            place *= 10
            let randomNumber = arc4random_uniform(10)
            finalNumber += Int(randomNumber) * place
        }
        return finalNumber
    }
    
    //更改状态栏背景颜色
    func changeStatusBarBackColor(color: UIColor!) {
        if #available(iOS 13.0, *) {
            let statusBar : UIView = UIView.init(frame: UIApplication.shared.keyWindow?.windowScene?.statusBarManager?.statusBarFrame ?? CGRect.init(x: 0, y: 0, width: ARScreenWidth, height: 20))
            statusBar.backgroundColor = color
            UIApplication.shared.keyWindow?.addSubview(statusBar)
            
        } else {
            // Fallback on earlier versions
            let statusBarWindow : UIView = UIApplication.shared.value(forKey: "statusBarWindow") as! UIView
            let statusBar : UIView = statusBarWindow.value(forKey: "statusBar") as! UIView
            if statusBar.responds(to:#selector(setter: UIView.backgroundColor)) {
                        statusBar.backgroundColor = color
            }
        }
    }
    
    //json转字典
    func getDictionaryFromJSONString(jsonString:String) ->NSDictionary{
        
        let jsonData:Data = jsonString.data(using: .utf8)!
        
        let dict = try? JSONSerialization.jsonObject(with: jsonData, options: .mutableContainers)
        if dict != nil {
            return dict as! NSDictionary
        }
        return NSDictionary()
    }
    
    //字典转json
    func getJSONStringFromDictionary(dictionary: NSDictionary) -> String {
        var result:String = ""
           do {
               //如果设置options为JSONSerialization.WritingOptions.prettyPrinted，则打印格式更好阅读
               let jsonData = try JSONSerialization.data(withJSONObject: dictionary, options: JSONSerialization.WritingOptions.init(rawValue: 0))

               if let JSONString = String(data: jsonData, encoding: String.Encoding.utf8) {
                   result = JSONString
               }
               
           } catch {
               result = ""
           }
           return result
    }
    
    //JSONString转换为数组
    func getArrayFromJSONString(jsonString:String) ->NSArray{
         
        let jsonData:Data = jsonString.data(using: .utf8)!
         
        let array = try? JSONSerialization.jsonObject(with: jsonData, options: .mutableContainers)
        if array != nil {
            return array as! NSArray
        }
        return array as! NSArray
    }
    
    //数组转json
    func getJSONStringFromArray(array:NSArray) -> String {
        if (!JSONSerialization.isValidJSONObject(array)) {
            print("无法解析出JSONString")
            return ""
        }
         
        let data : NSData! = try? JSONSerialization.data(withJSONObject: array, options: []) as NSData?
        let JSONString = NSString(data:data as Data,encoding: String.Encoding.utf8.rawValue)
        return JSONString! as String
    }
    
    //获取当前时间戳
    func getLocalDateTime() -> String {
        let date = Date()
        let timeFormatter = DateFormatter()
        //"yyy-MM-dd' at 'HH:mm:ss.SSS"
        timeFormatter.dateFormat = "yyy-MM-dd' 'HH:mm"
        let localTime = timeFormatter.string(from: date) as String
        return localTime
    }
    
    func isFullScreen() -> Bool {
        if #available(iOS 11, *) {
              guard let w = UIApplication.shared.delegate?.window, let unwrapedWindow = w else {
                  return false
              }
              
              if unwrapedWindow.safeAreaInsets.left > 0 || unwrapedWindow.safeAreaInsets.bottom > 0 {
                  print(unwrapedWindow.safeAreaInsets)
                  return true
              }
        }
        return false
    }
    
    //富文本
    func getAttributedString(text: String, image: UIImage, index: NSInteger) -> NSMutableAttributedString {
        
        if text.isEmpty {
            return NSMutableAttributedString()
        }
        
        let attri: NSMutableAttributedString = NSMutableAttributedString(string: text)
        let attch: NSTextAttachment = NSTextAttachment()
        attch.image = image
        attch.bounds = CGRect(x: 3, y: -5, width: 20, height: 20)
        
        let attrString: NSAttributedString = NSAttributedString(attachment: attch)
        attri.insert(attrString, at: index)
        return attri
    }
    
    //颜色
    public func changeFontColor(totalString: String, subString: String, font: UIFont, textColor: UIColor)-> NSMutableAttributedString {
        let attStr = NSMutableAttributedString.init(string: totalString)
        attStr.addAttributes([NSAttributedString.Key.foregroundColor: textColor, NSAttributedString.Key.font: font], range: NSRange.init(location: 0, length: subString.count))
        return attStr
    }
    
    //是否为空
    func isBlank(text: String?) -> Bool {
        if text == nil {
            return true
        }
        return text!.isEmpty
    }
    
    // 判断字符串是否全是空格 true 全是空格
    func stringAllIsEmpty(string: String) -> Bool {
        let trimmedStr = string.trimmingCharacters(in: .whitespacesAndNewlines)
        return trimmedStr.isEmpty
    }
    
    //取值
    func getAttributeValue(text: String?) -> String! {
        if text == nil || isBlank(text: text){
            return ""
        }
        return text
    }
    
    //创建or获取 录音地址
    func creatRecordPath() -> String {
        let manager = FileManager.default
        let baseUrl = NSHomeDirectory() + "/Library/Caches/Record/"
        let exist = manager.fileExists(atPath: baseUrl)
        if !exist {
            do {
                try manager.createDirectory(atPath: baseUrl, withIntermediateDirectories: true, attributes: nil)
                print("Succes to create folder")
            }
            catch {
                print("Error to create folder")
            }
        }
        return baseUrl
    }
    
    //layer
    func configRectCorner(view: UIView, corner: UIRectCorner, radii: CGSize) -> CALayer {
        let maskPath = UIBezierPath.init(roundedRect: view.bounds, byRoundingCorners: corner, cornerRadii: radii)
        let maskLayer = CAShapeLayer.init()
        maskLayer.frame = view.bounds
        maskLayer.path = maskPath.cgPath
        return maskLayer
   }
    
    //计算label高度
    func getLabelHeight(labelText: NSString, font: UIFont, width: CGFloat) -> CGFloat {
        let size = CGSize.init(width: width, height: 1000)
        let dic = NSDictionary(object: font,forKey: NSAttributedString.Key.font as NSCopying)
        let textSize = labelText.boundingRect(with: size, options: .usesLineFragmentOrigin, attributes: dic as? [NSAttributedString.Key : Any], context: nil)
        return textSize.height
    }
}

var gloabWindow: UIWindow?
var loadingView: UIView?
var loadingLabel: UILabel?

extension NSObject: CAAnimationDelegate {
    func initializeLoadingView() {
        loadingView?.removeFromSuperview()
        loadingView = nil
        //加载loading视图
        loadingView = UIView.init(frame: UIScreen.main.bounds)
        loadingView!.backgroundColor = RGBA(r: 0, g: 0, b: 0, a: 0.6)
        UIApplication.shared.keyWindow?.addSubview(loadingView!)
    }
    
    //加载视图
    func customLoadingView(text:String, count: Float) {
        if loadingView == nil {
            initializeLoadingView()
            let loadingImageView: UIImageView! = UIImageView.init(image: UIImage(named: "icon_loading"))
            loadingView?.addSubview(loadingImageView)
            loadingImageView.snp.makeConstraints { (make) in
                make.center.equalTo(loadingView!.snp.center)
                make.width.height.equalTo(51)
            }
            
            let animation = CABasicAnimation.init(keyPath: "transform.rotation.z")
            animation.duration = 2.0
            animation.fromValue = 0.0
            animation.toValue = Double.pi * 2
            animation.repeatCount = count
            animation.isRemovedOnCompletion = false
            animation.delegate = self
            loadingImageView.layer.add(animation, forKey: "LoadingAnimation")
            
            loadingLabel = UILabel.init()
            loadingLabel!.text = text
            loadingLabel!.textColor = UIColor.init(hexString: "#9FA3B6")
            loadingLabel!.font = UIFont(name: "PingFang SC", size: 14)
            loadingView?.addSubview(loadingLabel!)
            loadingLabel!.snp.makeConstraints({ (make) in
                make.centerX.equalTo(loadingImageView.snp.centerX)
                make.top.equalTo(loadingImageView.snp.bottom).offset(26)
            })
        }
    }
    
    //移除视图
    @objc func removeLoadingView() {
        loadingView?.removeFromSuperview()
        loadingView = nil
    }
    
    //延迟移除等待视图
    @objc func removeLoadingViewDelay(text: String?) {
        if loadingLabel != nil {
            loadingLabel?.text = text
            self.perform(#selector(removeLoadingView), with: nil, afterDelay: 2.0);
        }
    }
    
    public func animationDidStop(_ anim: CAAnimation, finished flag: Bool) {
        removeLoadingView()
    }
}


extension UIViewController {
    
    func getGloabWindow() -> UIWindow! {
        //自定义window
        if (gloabWindow == nil) {
            gloabWindow = UIWindow.init(frame: UIScreen.main.bounds)
            let currentKeyWindow = UIApplication.shared.keyWindow
            gloabWindow?.backgroundColor = UIColor.clear
            gloabWindow?.isHidden = false
            gloabWindow?.makeKeyAndVisible()
            gloabWindow?.windowLevel = .normal
            currentKeyWindow?.makeKey()
        }
        return gloabWindow
    }
    
    func dismissGloabWindow() {
        if (gloabWindow != nil) {
            gloabWindow?.removeFromSuperview()
            gloabWindow = nil
        }
    }
    
    func topViewController() -> UIViewController{
        var resultVc: UIViewController
        resultVc = topViewController(vc: UIApplication.shared.keyWindow!.rootViewController!)!
        while ((resultVc.presentedViewController) != nil) {
            resultVc = topViewController(vc: resultVc.presentedViewController!)!
        }
        return resultVc;
    }
    
    func topViewController(vc: UIViewController) -> UIViewController? {
        if vc is UINavigationController {
            let navVc: UINavigationController! = (vc as! UINavigationController)
            return topViewController(vc: navVc.topViewController!)
        } else if (vc is UITabBarController) {
            let tabBarVc: UITabBarController! = (vc as! UITabBarController)
            return topViewController(vc: tabBarVc.selectedViewController!)
        } else {
            return vc
        }
    }
}

extension CALayer {
    var borderColorFromUIColor: UIColor {
        get {
            return UIColor(cgColor: self.borderColor!)
        } set {
            self.borderColor = newValue.cgColor
        }
    }
}

extension UIView {

    //x position
    var x : CGFloat{
        
        get {
            return frame.origin.x
        }

        set(newVal){
            var tempFrame : CGRect = frame
            tempFrame.origin.x     = newVal
            frame                  = tempFrame
        }
    }


    //y position
    var y : CGFloat{

        get {

            return frame.origin.y

        }


        set(newVal){

            var tempFrame : CGRect = frame
            tempFrame.origin.y     = newVal
            frame                  = tempFrame

        }
    }


    //height
    var height : CGFloat{

        get {

            return frame.size.height

        }

        set(newVal){

            var tmpFrame : CGRect = frame
            tmpFrame.size.height  = newVal
            frame                 = tmpFrame

        }
    }


    // width
    var width : CGFloat {

        get {

            return frame.size.width
        }

        set(newVal) {

            var tmpFrame : CGRect = frame
            tmpFrame.size.width   = newVal
            frame                 = tmpFrame
        }
    }



    // left
    var left : CGFloat {

        get {

            return x
        }

        set(newVal) {

            x = newVal
        }
    }


    // right
    var right : CGFloat {

        get {

            return x + width
        }

        set(newVal) {

            x = newVal - width
        }
    }


    // top
    var top : CGFloat {

        get {

            return y
        }

        set(newVal) {

            y = newVal
        }
    }

    // bottom
    var bottom : CGFloat {

        get {

            return y + height
        }

        set(newVal) {

            y = newVal - height
        }
    }

    //centerX
    var centerX : CGFloat {

        get {

            return center.x
        }

        set(newVal) {

            center = CGPoint(x: newVal, y: center.y)
        }
    }

    //centerY
    var centerY : CGFloat {

        get {

            return center.y
        }

        set(newVal) {

            center = CGPoint(x: center.x, y: newVal)
        }
    }
    //middleX
    var middleX : CGFloat {

        get {

            return width / 2
        }
    }

    //middleY
    var middleY : CGFloat {

        get {

            return height / 2
        }
    }

    //middlePoint
    var middlePoint : CGPoint {

        get {

            return CGPoint(x: middleX, y: middleY)
        }
    }
}

extension UITableViewCell {
    
    private func _tableView() -> UITableView? {
        if let tableView = self.superview as? UITableView {
            return tableView
        }
        
        if let tableView = self.superview?.superview as? UITableView {
            return tableView
        }
        return nil
    }
    
    func addSectionCorner(at indexPath: IndexPath, radius: CGFloat = 10) {
        let tableView = self._tableView()!
        let rows = tableView.numberOfRows(inSection: indexPath.section)
        if indexPath.row == 0 || indexPath.row == rows - 1 {
            var corner: UIRectCorner
            if rows == 1 {
                corner = UIRectCorner.allCorners
            } else if indexPath.row == 0 {
                let cornerRawValue = UIRectCorner.topLeft.rawValue | UIRectCorner.topRight.rawValue
                corner = UIRectCorner(rawValue: cornerRawValue)
            } else {
                let cornerRawValue = UIRectCorner.bottomLeft.rawValue | UIRectCorner.bottomRight.rawValue
                corner = UIRectCorner(rawValue: cornerRawValue)
            }
            let cornerLayer = CAShapeLayer()
            cornerLayer.masksToBounds = true
            cornerLayer.frame = self.bounds
            let path = UIBezierPath(roundedRect: bounds, byRoundingCorners: corner, cornerRadii: CGSize(width: radius, height: radius))
            cornerLayer.path = path.cgPath
            self.layer.mask = cornerLayer
        } else {
            self.layer.mask = nil
        }
    }
}

//MARK:- 按钮扩展

enum EdgeInsetsStyle: Int {
    case Top, Left, Bottom, Right
}

extension UIButton {
    //按钮文字图片显示
    func layoutButtonWithEdgeInsetsStyle(style: EdgeInsetsStyle, space: CGFloat) {
        let imageWith: CGFloat = self.imageView!.frame.size.width;
        let imageHeight: CGFloat = self.imageView!.frame.size.height;
        
        var labelWidth: CGFloat = 0.0;
        var labelHeight: CGFloat = 0.0;
        if #available(iOS 8.0, *) {
            // 由于iOS8中titleLabel的size为0，用下面的这种设置
            labelWidth = self.titleLabel!.intrinsicContentSize.width
            labelHeight = self.titleLabel!.intrinsicContentSize.height
        } else {
            labelWidth = self.titleLabel!.frame.size.width
            labelHeight = self.titleLabel!.frame.size.height
        }
        
        var imageEdgeInsets: UIEdgeInsets = UIEdgeInsets.zero;
        var labelEdgeInsets: UIEdgeInsets = UIEdgeInsets.zero;
        
        switch (style) {
        case .Top:
            imageEdgeInsets = UIEdgeInsets(top: -labelHeight-space/2.0, left: 0, bottom: 0, right: -labelWidth)
            labelEdgeInsets = UIEdgeInsets(top: 0, left: -imageWith, bottom: -imageHeight-space/2.0, right: 0)
            break
        case .Left:
            imageEdgeInsets = UIEdgeInsets(top: 0, left: -space/2.0, bottom: 0, right: space/2.0)
            labelEdgeInsets = UIEdgeInsets(top: 0, left: space/2.0, bottom: 0, right: -space/2.0)
            break
            
        case .Bottom:
            imageEdgeInsets = UIEdgeInsets(top: 0, left: 0, bottom: -labelHeight-space/2.0, right: -labelWidth)
            labelEdgeInsets = UIEdgeInsets(top: -imageHeight-space/2.0, left: -imageWith, bottom: 0, right: 0)
            break
            
        case .Right:
            imageEdgeInsets = UIEdgeInsets(top: 0, left: labelWidth+space/2.0, bottom: 0, right: -labelWidth-space/2.0)
            labelEdgeInsets = UIEdgeInsets(top: 0, left: -imageWith-space/2.0, bottom: 0, right: imageWith+space/2.0)
            break
        }
        
        self.titleEdgeInsets = labelEdgeInsets;
        self.imageEdgeInsets = imageEdgeInsets;
    }
}

extension UIColor {
    convenience init(hexString:String){
        //处理数值
        var cString = hexString.uppercased().trimmingCharacters(in: CharacterSet.whitespacesAndNewlines)
        
        let length = (cString as NSString).length
        //错误处理
        if (length < 6 || length > 7 || (!cString.hasPrefix("#") && length == 7)){
            //返回whiteColor
            self.init(red: 0.0, green: 0.0, blue: 0.0, alpha: 1.0)
            return
        }
        
        if cString.hasPrefix("#"){
            cString = (cString as NSString).substring(from: 1)
        }
        
        //字符chuan截取
        var range = NSRange()
        range.location = 0
        range.length = 2
        
        let rString = (cString as NSString).substring(with: range)
        
        range.location = 2
        let gString = (cString as NSString).substring(with: range)
        
        range.location = 4
        let bString = (cString as NSString).substring(with: range)
        
        //存储转换后的数值
        var r:UInt32 = 0,g:UInt32 = 0,b:UInt32 = 0
        //进行转换
        Scanner(string: rString).scanHexInt32(&r)
        Scanner(string: gString).scanHexInt32(&g)
        Scanner(string: bString).scanHexInt32(&b)
        //根据颜色值创建UIColor
         self.init(red: CGFloat(r)/255.0, green: CGFloat(g)/255.0, blue: CGFloat(b)/255.0, alpha: 1.0)
    }
}

extension UIImage {
    class func imageWithColor(_ color: UIColor) -> UIImage {
        
        let rect = CGRect(x: 0, y: 0, width: 1.0, height: 1.0)
        UIGraphicsBeginImageContext(rect.size)
        let context = UIGraphicsGetCurrentContext()
        context?.setFillColor(color.cgColor)
        context?.fill(rect)
        let image = UIGraphicsGetImageFromCurrentImageContext()
        UIGraphicsEndImageContext()
        
        return image!
    }
}

extension UITextField {
   @IBInspectable var placeHolderColor: UIColor? {
        get {
            return self.placeHolderColor
        }
        set {
            self.attributedPlaceholder = NSAttributedString(string:self.placeholder != nil ? self.placeholder! : "", attributes:[NSAttributedString.Key.foregroundColor: newValue!])
        }
    }
}

extension UIResponder {
    static let anyHouseNotificationRefreshHandsUpList = Notification.Name(rawValue: "anyHouseNotificationRefreshHandsUpList")
    static let anyHouseNotificationInvitation = Notification.Name(rawValue: "anyHouseNotificationInvitation")
}


extension UserDefaults {
    enum AccountKeys: String {
        case uid
        case userToken
        case userName
        case appId
        case avatar
        case isLogin
    }
    
    static func set(value: String, forKey key: AccountKeys) {
        let key = key.rawValue
        UserDefaults.standard.set(value, forKey: key)
    }

    static func string(forKey key: AccountKeys) -> String? {
        let key = key.rawValue
        return UserDefaults.standard.string(forKey: key)
    }
}

extension UIViewController {
    func createBarButtonItem(title: String?) -> UIBarButtonItem {
        let leftButton = UIButton.init(type: .custom)
        leftButton.setTitle(title, for: .normal)
        leftButton.setImage(UIImage(named: "icon_back"), for: .normal)
        leftButton.titleLabel?.font = UIFont(name: "PingFang SC", size: 18)
        leftButton.titleLabel?.textColor = UIColor.white
        leftButton.layoutButtonWithEdgeInsetsStyle(style: .Left, space: 10)
        leftButton.addTarget(self, action: #selector(popBack), for: .touchUpInside)
        self.navigationItem.leftBarButtonItem = UIBarButtonItem(customView: leftButton)
        return UIBarButtonItem(customView: leftButton)
    }
    
    @objc func popBack() {
        self.navigationController?.popViewController(animated: true)
    }
}

class ARCollectionView: UICollectionView {
    override func reloadData() {
        UIView.performWithoutAnimation {
            super.reloadData()
        }
    }
    
    override func reloadSections(_ sections: IndexSet) {
        UIView.performWithoutAnimation {
            super.reloadSections(sections)
        }
    }
}



