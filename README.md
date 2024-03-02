# 星星相机-Star Camera
具有发光点识别+星星特效功能的安卓应用

从给定的图像中识别出发光点，并在识别到的发光点周围绘制出星星、闪烁效果，以实现画面闪闪发光，氛围梦幻的视觉效果

## 界面设计
| 主页 | 预览页 | 结果页 |
| --- | --- | --- |
| ![主页](https://github.com/zoemzy/Star-Camera/blob/main/images/home_page.png) | ![预览页](https://github.com/zoemzy/Star-Camera/blob/main/images/preview_page.png) | ![结果页](https://github.com/zoemzy/Star-Camera/blob/main/images/result_page.png) |

## 功能概述
### 1. 获取权限
依据不同安卓版本，向用户申请相机和读取外部存储权限，以访问相机和相册

![获取权限](https://github.com/zoemzy/Star-Camera/blob/main/images/permission.gif)
### 2. 从相机拍摄
点击“相机”按钮，调用手机摄像头拍摄照片

![从相机拍摄](https://github.com/zoemzy/Star-Camera/blob/main/images/camera.gif)
### 3. 从相册导入
点击“相册”按钮，访问系统相册选择导入图片

![从相册导入](https://github.com/zoemzy/Star-Camera/blob/main/images/album.gif)
### 4. 参数控制与实时渲染
参数控制：提供可调节的参数，如星星的大小、亮度、密度等，以便用户能够根据需要进行调整

实时渲染：闪光效果可在画面中实时预览，并跟随用户的参数调节而变化
| 发光点亮度 | 发光点大小 | 星星大小 |
| :---: | :---: | :---: |
| ![发光点亮度](https://github.com/zoemzy/Star-Camera/blob/main/images/point_brightness.gif) | ![发光点大小](https://github.com/zoemzy/Star-Camera/blob/main/images/point_size.gif) | ![星星大小](https://github.com/zoemzy/Star-Camera/blob/main/images/star_size.gif) |
| **星星透明度** | **星星亮度** | **星星颜色** |
| ![星星透明度](https://github.com/zoemzy/Star-Camera/blob/main/images/star_transparency.gif) | ![星星亮度](https://github.com/zoemzy/Star-Camera/blob/main/images/star_brightness.gif) | ![星星颜色](https://github.com/zoemzy/Star-Camera/blob/main/images/star_color.gif)|
| **星星样式** |
| ![星星样式](https://github.com/zoemzy/Star-Camera/blob/main/images/star_pattern.gif) |

### 5. 图片保存
点击“保存图片”按钮，处理后的图片将被保存在系统相册“星星相机”中

![图片保存](https://github.com/zoemzy/Star-Camera/blob/main/images/save_image.gif)
