//
//  ARAudioViewCell.swift
//  anyHouse-iOS
//
//  Created by 余生丶 on 2021/3/29.
//

import UIKit
import AttributedString

class ARMainTableViewCell: UITableViewCell {
    @IBOutlet weak var roomNameLabel: UILabel!
    @IBOutlet weak var imageView0: UIImageView!
    @IBOutlet weak var imageView1: UIImageView!
    @IBOutlet weak var nameLabel: UILabel!
    @IBOutlet weak var numberLabel: UILabel!
    
    var listModel: ARAudioRoomListModel? {
        didSet {
            var imageName0: String?
            var imageName1: String?
            for avatar in listModel!.avatars.enumerated() {
                if avatar.offset == 0 {
                    imageName0 = headListArr![Int(avatar.element)! - 1] as? String
                    imageView0.image = UIImage(named: imageName0 ?? "icon_head1")
                } else {
                    imageName1 = headListArr![Int(avatar.element)! - 1] as? String
                    imageView1.image = UIImage(named: imageName1 ?? "icon_head1")
                }
            }
            
            if listModel?.isPrivate == 1 {
                roomNameLabel.attributed.text = .init("""
                \(listModel?.roomName ?? "") \(.image(#imageLiteral(resourceName: "icon_room_lock"), .custom(.center, size: .init(width: 20, height: 20))))
                """)
            } else {
                roomNameLabel.attributed.text = .init("""
                \(listModel?.roomName ?? "") \(.image(#imageLiteral(resourceName: "icon_room"), .custom(.center, size: .init(width: 20, height: 20))))
                """)
            }
            
            var attributeText: AttributedString = AttributedString(NSAttributedString())
            for userModel in listModel!.userList {
                let name: AttributedString = .init("""
                    \(userModel.userName!) \(.image(#imageLiteral(resourceName: "icon_speaker"), .custom(.center, size: .init(width: 20, height: 20)))) \n
                """)
                attributeText += name
            }
            nameLabel.attributed.text = attributeText
            
            numberLabel.attributed.text = .init("""
                \(listModel?.userTotalNum ?? 0)\(.image(#imageLiteral(resourceName: "icon_user"), .custom(.center, size: CGSize.init(width: 24, height: 24))))  \(listModel?.speakerTotalNum ?? 0)\(.image(#imageLiteral(resourceName: "icon_speakers"), .custom(.center, size: CGSize.init(width: 24, height: 24))))
            """)
        }
    }
}

class ARAudioViewCell: UICollectionViewCell {
    @IBOutlet weak var avatarImageView: UIImageView!
    @IBOutlet weak var nameLabel: UILabel!
    @IBOutlet weak var audioImageView: UIImageView!
    
    func updateCell(micModel: ARMicModel?, width: CGFloat) {
        let avatar = (micModel?.avatar ?? 1) - 1
        avatarImageView.image = UIImage(named: headListArr![avatar] as! String)
        nameLabel.text = micModel?.userName
        audioImageView.isHidden = (micModel?.enableAudio != 0)
        avatarImageView.layer.cornerRadius = (width - 24)/2
    }
}

class ARAudioCollectionReusableView: UICollectionReusableView {
    @IBOutlet weak var titleLabel: UILabel!
    
    func update(section: NSInteger, model: ARRoomInfoModel?) {
        if section == 0 {
            titleLabel.text = model?.roomName
            if model?.isPrivate == 1 {
                titleLabel.attributed.text = .init("""
                \(model?.roomName ?? "") \(.image(#imageLiteral(resourceName: "icon_room_lock"), .custom(.center, size: .init(width: 20, height: 20))))
                """)
            } else {
                titleLabel.attributed.text = .init("""
                \(model?.roomName ?? "") \(.image(#imageLiteral(resourceName: "icon_room"), .custom(.center, size: .init(width: 20, height: 20))))
                """)
            }
            titleLabel.textColor = UIColor(hexString: "#2E3135")
            titleLabel.font = UIFont(name: "PingFangSC-Semibold", size: 16)
        } else {
            titleLabel.text = "听众"
            titleLabel.textColor = UIColor(hexString: "#E0E0E0")
            titleLabel.font = UIFont(name: "PingFangSC-Semibold", size: 14)
        }
    }
}

class ARMicCell: UITableViewCell {
    @IBOutlet weak var avatarImageView: UIImageView!
    @IBOutlet weak var nameLabel: UILabel!
    @IBOutlet weak var invitationButton: UIButton!
    
    var onButtonTapped : (() -> Void)? = nil
    
    var micModel: ARMicModel? {
        didSet {
            nameLabel.text = micModel?.userName
            let avatar = Int(micModel?.avatar ?? 1) - 1
            avatarImageView.image = UIImage(named: headListArr![avatar] as! String)
            if micModel?.state == -1 {
                invitationButton.isUserInteractionEnabled = false
                invitationButton.backgroundColor = UIColor(hexString: "#EEEEEE")
                invitationButton.isSelected = true
            } else if micModel?.state == 1 {
                invitationButton.isUserInteractionEnabled = true
                invitationButton.backgroundColor = UIColor(hexString: "#4BAB63")
                invitationButton.isSelected = false
            }
        }
    }
    
    @IBAction func didClickControlButton(_ sender: Any) {
        if let onButtonTapped = self.onButtonTapped {
            onButtonTapped()
        }
    }
}

class ARGeneralTableViewCell: UITableViewCell {
    override var frame: CGRect {
        get {
            return super.frame
        }
        set {
            var frame = newValue
            frame.origin.x += 15
            frame.size.width -= 2 * 15
            super.frame = frame
        }
    }
}
