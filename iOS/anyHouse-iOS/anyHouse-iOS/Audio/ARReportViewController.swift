//
//  ARReportViewController.swift
//  anyHouse-iOS
//
//  Created by 余生丶 on 2021/4/8.
//

import UIKit
import Alamofire
import AttributedString

class ARReportViewController: UIViewController {
    
    @IBOutlet weak var headerLabel: UILabel!
    @IBOutlet weak var backView: UIView!
    @IBOutlet weak var collectionView: UICollectionView!
    
    private let reuseIdentifier = "anyHouse_Report_CellID"
    private var flowLayout: UICollectionViewFlowLayout? = {
        let layout = UICollectionViewFlowLayout.init()
        layout.sectionInset = UIEdgeInsets(top: 0, left: 10, bottom: 0, right: 10)
        layout.scrollDirection = .vertical
        layout.minimumLineSpacing = 19
        layout.minimumInteritemSpacing = 31
        return layout
    }()
    let tap = UITapGestureRecognizer()
    
    var menus: [ARReportItem] = [
        ARReportItem(text: "政治敏感", selected: true),
        ARReportItem(text: "非法广告", selected: false),
        ARReportItem(text: "淫秽色情", selected: false),
        ARReportItem(text: "其他违法", selected: false)
    ]
    
    override func viewDidLoad() {
        super.viewDidLoad()

        // Do any additional setup after loading the view.
        headerLabel.attributed.text = .init("""
        \(.image(#imageLiteral(resourceName: "icon_report"), .custom(.center, size: .init(width: 22, height: 22)))) 举报
        """)
        
        tap.delegate = self
        self.view.addGestureRecognizer(tap)
        collectionView.collectionViewLayout = flowLayout!
    }
    
    @IBAction func didClickButton(_ sender: Any) {
        let manager = NetworkReachabilityManager()
        if manager?.networkReachabilityStatus != .notReachable {
            SVProgressHUD.showSuccess(withStatus: "举报成功！")
        } else {
            SVProgressHUD.showError(withStatus: "举报失败！")
        }
        SVProgressHUD.dismiss(withDelay: 0.5)
        self.dismiss(animated: false, completion: nil)
    }
}

extension ARReportViewController: UICollectionViewDelegate, UICollectionViewDataSource, UICollectionViewDelegateFlowLayout {
    
    func collectionView(_ collectionView: UICollectionView, numberOfItemsInSection section: Int) -> Int {
        return menus.count
    }
    
    func collectionView(_ collectionView: UICollectionView, cellForItemAt indexPath: IndexPath) -> UICollectionViewCell {
        let collectionViewCell: ARReportCollectionViewCell! = (collectionView.dequeueReusableCell(withReuseIdentifier: reuseIdentifier, for: indexPath) as! ARReportCollectionViewCell)
        // Configure the cell
        collectionViewCell.menuItem = menus[indexPath.row]
        return collectionViewCell
    }
    
    func collectionView(_ collectionView: UICollectionView, layout collectionViewLayout: UICollectionViewLayout, sizeForItemAt indexPath: IndexPath) -> CGSize {
        return CGSize.init(width: 88, height: 44)
    }
    
    func collectionView(_ collectionView: UICollectionView, didSelectItemAt indexPath: IndexPath) {
        for index in 0..<menus.count {
            let menuItem = menus[index]
            menuItem.isSelected = false
            if index == indexPath.row {
                menuItem.isSelected = true
            }
        }
        collectionView.reloadData()
    }
}

extension ARReportViewController: UIGestureRecognizerDelegate {
    func gestureRecognizer(_ gestureRecognizer: UIGestureRecognizer, shouldReceive touch: UITouch) -> Bool {
        if(touch.view == self.view) {
            self.dismiss(animated: false, completion: nil)
            return true
        } else {
            return false
        }
    }
}
