//
//  ARMicViewController.swift
//  anyHouse-iOS
//
//  Created by 余生丶 on 2021/3/24.
//

import UIKit

class ARMicViewController: UIViewController {
    @IBOutlet weak var tableView: UITableView!
    @IBOutlet weak var backView: UIView!
    
    var roomId: String!
    var listMicArr = [ARMicModel]()
    let tap = UITapGestureRecognizer()
    
    lazy var placeholderView: ARPlaceholderView = {
        let placeholderView = ARPlaceholderView()
        return ARPlaceholderView()
    }()
    
    override func viewDidLoad() {
        super.viewDidLoad()

        // Do any additional setup after loading the view.
        backView.layer.maskedCorners = [.layerMinXMinYCorner,.layerMaxXMinYCorner]
        getHandsUpList()
        tap.delegate = self
        self.view.addGestureRecognizer(tap)
        NotificationCenter.default.addObserver(self, selector: #selector(getHandsUpList), name: UIResponder.anyHouseNotificationRefreshHandsUpList, object: nil)
    }
    
    @objc func createPlaceholder() {
        placeholderView.showPlaceholderView(self.tableView, placeholderImageName: "icon_add", placeholderTitle: "暂无听众举手") {
            self.tableView.mj_header?.beginRefreshing()
        }
        placeholderView.backgroundColor = UIColor.clear
    }
    
    @objc private func getHandsUpList() {
        //获取举手列表
        let parameters : NSDictionary = ["roomId": roomId as Any]
        ARNetWorkHepler.getResponseData("getHandsUpList", parameters: parameters as? [String : AnyObject], headers: true, success: { [weak self](result) in
            if result["code"] == 0 {
                self?.listMicArr.removeAll()
                let jsonArr = result["data"].arrayValue
                for json in jsonArr {
                    self?.listMicArr.append(ARMicModel(jsonData: json))
                }
            }
            self?.listMicArr.count == 0 ? (self?.createPlaceholder()) : self?.placeholderView.removeFromSuperview()
            self?.tableView.reloadData()
        }) { (error) in

        }
    }
}

//MARK: - UIGestureRecognizerDelegate

extension ARMicViewController: UIGestureRecognizerDelegate {
    func gestureRecognizer(_ gestureRecognizer: UIGestureRecognizer, shouldReceive touch: UITouch) -> Bool {
        if(touch.view == self.view) {
            self.dismiss(animated: false, completion: nil)
            return true
        } else {
            return false
        }
    }
}

//MARK: - UITableViewDelegate, UITableViewDataSource

extension ARMicViewController: UITableViewDelegate, UITableViewDataSource {
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return listMicArr.count
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell: ARMicCell = tableView.dequeueReusableCell(withIdentifier: "ARMicCellID") as! ARMicCell
        let micModel = listMicArr[indexPath.row]
        cell.micModel = micModel
        cell.onButtonTapped = { () in
            cell.micModel?.state = -1
            tableView.reloadData()
            NotificationCenter.default.post(name: UIResponder.anyHouseNotificationInvitation, object: self, userInfo: ["uid": micModel.uid as Any])
        }
        cell.selectionStyle = .none
        return cell
    }
}
