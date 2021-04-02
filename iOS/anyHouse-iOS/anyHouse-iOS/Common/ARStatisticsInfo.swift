//
//  ARStatisticsInfo.swift
//  AudioLive-iOS
//
//  Created by 余生丶 on 2021/3/2.
//

import UIKit
import SwiftyJSON
import ARtcKit

enum ARMicStatus {
    case normal
    case cancle
    case exist
}

struct ARAudioRoomListModel {
    var ownerUid: String?
    var isBroadcaster: Bool = false
    var avatars = [String]()
    var userTotalNum: NSInteger?
    var roomPwd: String?
    var roomId: String?
    //是否私密房间(0:非私密房间 1:私密房间)
    var isPrivate: NSInteger = 0
    var speakerTotalNum: NSInteger = 0
    var roomName: String?
    var userList = [ARUserModel]()
    
    init(jsonData: JSON) {
        ownerUid = jsonData["ownerUid"].stringValue
        if ownerUid == UserDefaults.string(forKey: .uid) {
            isBroadcaster = true
        }
        userTotalNum = jsonData["userTotalNum"].intValue
        roomPwd = jsonData["roomPwd"].stringValue
        roomId = jsonData["roomId"].stringValue
        isPrivate = jsonData["isPrivate"].intValue
        speakerTotalNum = jsonData["speakerTotalNum"].intValue
        roomName = jsonData["roomName"].stringValue
        for avatar in jsonData["avatars"].arrayValue {
            avatars.append(avatar.stringValue)
        }
        
        for user in jsonData["userList"].arrayValue {
            userList.append(ARUserModel(jsonData: user))
        }
    }
}

struct ARUserModel {
    var uid: String?
    var userName: String?
    var avatar: String?
    
    init(jsonData: JSON) {
        uid = jsonData["uid"].stringValue
        userName = jsonData["userName"].stringValue
        avatar = jsonData["avatar"].stringValue
    }
}


struct ARRoomInfoModel {
    var isBroadcaster: Bool = false
    //房间状态(1:进行中;2:结束)
    var state: NSInteger?
    var rtmToken: String?
    var ownerId: String?
    var avatar: String = "1"
    var roomName: String?
    var userName: String?
    var roomId: String?
    var rtcToken: String?
    var isPrivate: NSInteger = 0
    
    init(jsonData: JSON) {
        state = jsonData["state"].intValue
        rtmToken = jsonData["rtmToken"].stringValue
        ownerId = jsonData["ownerId"].stringValue
        if ownerId == UserDefaults.string(forKey: .uid) {
            isBroadcaster = true
        }
        if jsonData["avatar"].stringValue.count != 0 {
            avatar = headListArr![(Int(jsonData["avatar"].stringValue)! - 1)] as! String
        }
        roomName = jsonData["roomName"].stringValue
        userName = jsonData["userName"].stringValue
        roomId = jsonData["roomId"].stringValue
        rtcToken = jsonData["rtcToken"].stringValue
        isPrivate = jsonData["isPrivate"].intValue
    }
}

class ARMicModel: NSObject {
    var userName: String?
    var uid: String?
    var avatar: NSInteger?
    var enableAudio: NSInteger = 1
    //-1:邀请过;1:占麦
    var state: NSInteger = 0
    
    init(jsonData: JSON) {
        userName = jsonData["userName"].stringValue
        uid = jsonData["uid"].stringValue
        avatar = jsonData["avatar"].intValue
        enableAudio = jsonData["enableAudio"].intValue
        state = jsonData["state"].intValue
    }
}

extension NSObject {
    func login(userName: String) {
        //登录
        SVProgressHUD.show(withStatus: "登录中...")
        let parameters : NSDictionary = ["cType": 2, "userName": userName as Any, "pkg": Bundle.main.infoDictionary!["CFBundleIdentifier"] as Any]
        ARNetWorkHepler.getResponseData("signIn", parameters: parameters as? [String : AnyObject], headers: false, success: { (result) in
            if result["code"] == 0 {
                UserDefaults.set(value: result["data"]["avatar"].stringValue , forKey: .avatar)
                UserDefaults.set(value: result["data"]["userName"].stringValue , forKey: .userName)
                UserDefaults.set(value: result["data"]["userToken"].stringValue , forKey: .userToken)
                UserDefaults.set(value: result["data"]["appId"].stringValue , forKey: .appId)
                UserDefaults.set(value: result["data"]["uid"].stringValue , forKey: .uid)
                
                let storyboard = UIStoryboard.init(name: "Main", bundle: nil)
                guard let navVc = storyboard.instantiateViewController(withIdentifier: "anyHouse_Main") as? UINavigationController else { return }
                UIApplication.shared.keyWindow?.rootViewController = navVc
            }
            SVProgressHUD.dismiss(withDelay: 1)
        }) { (error) in
            SVProgressHUD.dismiss(withDelay: 1)
        }
    }
    
    func leaveRoom(roomId: String, broadcaster: Bool) {
        //离开房间
        let parameters : NSDictionary = ["roomId": roomId as Any, "isOwner": broadcaster ? 1 : 0]
        ARNetWorkHepler.getResponseData("leaveRoom", parameters: parameters as? [String : AnyObject], headers: true, success: { (result) in
            print("leaveRoom: \(result) \n\(parameters)")
        }) { (error) in
            
        }
    }
    
    func updateUserStatus(roomId: String, status: NSInteger, uid: String?) {
        //更新用户在房间的状态
        //status -1:邀请听众;0:拒绝邀请,取消举手,拒绝举手,下麦;1:举手;2:同意举手,同意邀请
        let parameters : NSDictionary = ["roomId": roomId as Any, "status": status, "uid": uid as Any]
        ARNetWorkHepler.getResponseData("updateUserStatus", parameters: parameters as? [String : AnyObject], headers: true, success: { (result) in
            
        }) { (error) in
            
        }
    }
}
