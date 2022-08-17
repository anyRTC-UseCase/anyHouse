//
//  ARAudioViewCell.swift
//  anyHouse-iOS
//
//  Created by 余生丶 on 2021/3/29.
//

import AttributedString
import UIKit

class ARMainTableViewCell: UITableViewCell {
    @IBOutlet var roomNameLabel: UILabel!
    @IBOutlet var imageView0: UIImageView!
    @IBOutlet var imageView1: UIImageView!
    @IBOutlet var nameLabel: UILabel!
    @IBOutlet var numberLabel: UILabel!
    @IBOutlet var blackButton: UIButton!
    
    var onButtonTapped: (() -> Void)?
    
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
            
            var attributeText = ASAttributedString(NSAttributedString())
            for userModel in listModel!.userList {
                let name = ASAttributedString.init("""
                    \(userModel.userName!) \(.image(#imageLiteral(resourceName: "icon_speaker"), .custom(.center, size: .init(width: 20, height: 20)))) \n
                """)
                attributeText += name
            }
            nameLabel.attributed.text = attributeText
            
            numberLabel.attributed.text = .init("""
                \(listModel?.userTotalNum ?? 0)\(.image(#imageLiteral(resourceName: "icon_user"), .custom(.center, size: CGSize(width: 24, height: 24))))  \(listModel?.speakerTotalNum ?? 0)\(.image(#imageLiteral(resourceName: "icon_speakers"), .custom(.center, size: CGSize(width: 24, height: 24))))
            """)
            blackButton.isHidden = (listModel?.ownerUid == UserDefaults.string(forKey: .uid))
        }
    }
    
    @IBAction func didClickBlacklistButton(_ sender: Any) {
        if let onButtonTapped = self.onButtonTapped {
            onButtonTapped()
        }
    }
}

class ARAudioCollectionReusableView: UICollectionReusableView {
    @IBOutlet var titleLabel: UILabel!
    
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
    @IBOutlet var avatarImageView: UIImageView!
    @IBOutlet var nameLabel: UILabel!
    @IBOutlet var invitationButton: UIButton!
    
    var onButtonTapped: (() -> Void)?
    
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

class ARAudioViewCell: UICollectionViewCell {
    @IBOutlet var avatarImageView: UIImageView!
    @IBOutlet var nameLabel: UILabel!
    @IBOutlet var audioImageView: UIImageView!
    
    private let radarAnimation = "radarAnimation"

    private var animationLayer: CALayer?
    private var animationGroup: CAAnimationGroup?
    private var isAnimation: Bool = false
    
    func updateCell(micModel: ARMicModel?, width: CGFloat, broadcaster: Bool) {
        let avatar = (micModel?.avatar ?? 1) - 1
        avatarImageView.image = UIImage(named: headListArr![avatar] as! String)
        audioImageView.isHidden = (micModel?.enableAudio != 0)
        avatarImageView.layer.cornerRadius = (width - 24)/2
        animationLayer?.removeAnimation(forKey: radarAnimation)
        if broadcaster {
            nameLabel.attributed.text = .init("""
            \(.image(#imageLiteral(resourceName: "icon_broadcaster"), .custom(.center, size: .init(width: 20, height: 20)))) \(micModel?.userName ?? "")
            """)
        } else {
            nameLabel.text = micModel?.userName
        }
    }
    
    func startAnimation() {
        if !isAnimation {
            animationLayer?.removeAnimation(forKey: radarAnimation)
            let second = makeRadarAnimation(showRect: avatarImageView.frame, isRound: false)
            contentView.layer.insertSublayer(second, below: avatarImageView.layer)
        }
    }
    
    private func makeRadarAnimation(showRect: CGRect, isRound: Bool) -> CALayer {
        let shapeLayer = CAShapeLayer()
        shapeLayer.frame = showRect
        if isRound {
            shapeLayer.path = UIBezierPath(ovalIn: CGRect(x: 0, y: 0, width: showRect.width, height: showRect.height)).cgPath
        } else {
            shapeLayer.path = UIBezierPath(roundedRect: CGRect(x: 0, y: 0, width: showRect.width, height: showRect.height), cornerRadius: (avatarImageView.width - 9)/2).cgPath
        }

        shapeLayer.fillColor = UIColor(hexString: "#CEBD7A").cgColor
        shapeLayer.opacity = 0.0

        animationLayer = shapeLayer

        let replicator = CAReplicatorLayer()
        replicator.frame = shapeLayer.bounds
        replicator.instanceCount = 4
        replicator.instanceDelay = 1.0
        replicator.addSublayer(shapeLayer)

        let opacityAnimation = CABasicAnimation(keyPath: "opacity")
        opacityAnimation.fromValue = NSNumber(floatLiteral: 1.0)
        opacityAnimation.toValue = NSNumber(floatLiteral: 0)

        let scaleAnimation = CABasicAnimation(keyPath: "transform")
        if isRound {
            scaleAnimation.fromValue = NSValue(caTransform3D: CATransform3DScale(CATransform3DIdentity, 0, 0, 0))
        } else {
            scaleAnimation.fromValue = NSValue(caTransform3D: CATransform3DScale(CATransform3DIdentity, 1.0, 1.0, 0))
        }
        scaleAnimation.toValue = NSValue(caTransform3D: CATransform3DScale(CATransform3DIdentity, 1.3, 1.3, 0))

        let animationGroup = CAAnimationGroup()
        animationGroup.animations = [opacityAnimation, scaleAnimation]
        animationGroup.duration = 1.0
        animationGroup.repeatCount = 1
        animationGroup.autoreverses = false
        animationGroup.delegate = self

        self.animationGroup = animationGroup

        shapeLayer.add(animationGroup, forKey: radarAnimation)

        return replicator
    }
    
    func animationDidStart(_ anim: CAAnimation) {
        isAnimation = true
    }
    
    override func animationDidStop(_ anim: CAAnimation, finished flag: Bool) {
        isAnimation = false
    }
}

class ARReportCollectionViewCell: UICollectionViewCell {
    @IBOutlet var titleLabel: UILabel!
    
    var menuItem: ARReportItem? {
        didSet {
            titleLabel.text = menuItem?.title
            if menuItem!.isSelected {
                titleLabel.layer.borderColor = UIColor(hexString: "#E75D5A").cgColor
                titleLabel.font = UIFont(name: "PingFangSC-Semibold", size: 12)
                titleLabel.textColor = UIColor(hexString: "#E75D5A")
            } else {
                titleLabel.layer.borderColor = UIColor(hexString: "#F1EFE5").cgColor
                titleLabel.font = UIFont(name: "PingFang SC", size: 14)
                titleLabel.textColor = UIColor(hexString: "#999999")
            }
        }
    }
}
