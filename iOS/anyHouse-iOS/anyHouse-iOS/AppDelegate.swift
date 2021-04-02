//
//  AppDelegate.swift
//  anyHouse-iOS
//
//  Created by 余生丶 on 2021/3/22.
//

import UIKit

@main
class AppDelegate: UIResponder, UIApplicationDelegate {

    var window: UIWindow?

    func application(_ application: UIApplication, didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]?) -> Bool {
        // Override point for customization after application launch.
        
        SVProgressHUD.setDefaultStyle(.dark)
        SVProgressHUD.setDefaultMaskType(.black)
        SVProgressHUD.setMinimumSize(CGSize.init(width: 110, height: 110))
        RunLoop.current.run(until: Date(timeIntervalSinceNow: 0.5))
        
        if UserDefaults.string(forKey: .uid)?.count ?? 0 > 0 {
            self.window = UIWindow.init(frame: UIScreen.main.bounds)
            self.window?.backgroundColor = UIColor.white

            let storyBoard = UIStoryboard.init(name: "Main", bundle: nil)
            let mainVc = storyBoard.instantiateViewController(withIdentifier: "anyHouse_Main") as? UINavigationController
            self.window?.rootViewController = mainVc
        }
        return true
    }
}

