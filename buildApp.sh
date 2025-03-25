#!/bin/bash
# 定义构建应用所需的工件ID（Artifact ID）
ARTIFACT_ID="agcs_biz_lib"
# 定义 JDK 的路径
JAVA_HOME="/Users/dingpeihua/java/jdk1.8.0_402.jdk/Contents/Home"
# 定义项目根目录
PROJECT_DIR="/Users/dingpeihua/AndroidStudioProjects/agcs-biz"
PROJECT_APP_DIR="$PROJECT_DIR/app"
LIBS_DIR="$PROJECT_APP_DIR/libs"
PROJECT_BUILD_DIR="$PROJECT_APP_DIR/build"
# 定义插件库目录
PLUGIN_LIBS_DIR="plugin_libs/com/alibaba/ailabs/genie/agcs_biz_lib/1.0.0.0-SNAPSHOT"

# 目标设备 ID
TARGET_DEVICE_ID="45000024171858010000008"
# 删除旧的 openApi-service*.aar 文件
echo "Deleting old $ARTIFACT_ID*.*.aar files in $LIBS_DIR..."
# 删除多个旧的 .aar 文件

rm -f "$LIBS_DIR/$ARTIFACT_ID*.aar"
rm -f "$LIBS_DIR/$ARTIFACT_ID*.*.aar"

# 确保目标目录存在
mkdir -p "$LIBS_DIR"

# 复制新的 .aar 文件到 libs 目录
echo "Copying new .aar files from $PLUGIN_LIBS_DIR to $LIBS_DIR..."

# 使用 find 命令查找并复制文件
find "$PLUGIN_LIBS_DIR" -name "$ARTIFACT_ID*.*.aar" -exec cp {} "$LIBS_DIR/" \;

if [ $? -eq 0 ]; then
    echo "Successfully copied the new .aar file(s)."
else
    echo "Failed to copy .aar file(s) from $PLUGIN_LIBS_DIR to $LIBS_DIR."
    exit 1
fi

# 进入项目根目录
cd "$PROJECT_DIR" || { echo "Failed to change directory to $PROJECT_DIR"; exit 1; }
# 切换到 JDK8
echo "Switching to JDK8..."
export JAVA_HOME="$JAVA_HOME"
# 清理并编译项目
echo "Cleaning and building the project..."
./gradlew clean app:assembleRelease || { echo "Build failed"; exit 1; }
# 查找所有设备并逐个设备安装
echo "Installing the app on all connected devices..."
# 获取连接的设备列表
connected_devices=$(adb devices | grep -v "List" | grep -v "offline" | awk '{print $1}')

# 检查连接的设备数量
device_count=$(echo "$connected_devices" | wc -l)

if [ "$device_count" -eq 0 ]; then
    echo "No connected devices found."
    exit 1
elif [ "$device_count" -gt 1 ]; then
    echo "Multiple devices connected. Please specify one device."
    adb devices
fi
# 显示设备列表
# 如果TARGET_DEVICE_ID为空则给出选择
if [ "$TARGET_DEVICE_ID" == "" ]; then
  echo "Select a device to install the APK:"
  select device in $connected_devices; do
        # 去除设备 ID 的多余空格
        cleaned_device=$(echo "$device" | tr -d ' ')
     if [ -n "$cleaned_device" ]; then
          echo "You selected: $cleaned_device"
          adb -s "$cleaned_device" install -d "$PROJECT_BUILD_DIR/outputs/apk/release/app-release.apk"
          break
      else
          echo "Invalid selection, please try again."
          break
      fi
  done
else
  ## 循环安装 APK
  while read device; do
      if [ "$device" == "$TARGET_DEVICE_ID" ]; then
          echo "Installing on the target device ID: $device"
          adb -s "$device" install -d "$PROJECT_BUILD_DIR/outputs/apk/release/app-release.apk"
          echo "Installed on $device"
          break
      else
         continue # 跳过不匹配的设备
      fi
  done <<< "$connected_devices"
fi
# 输出成功信息
echo "Build finished successfully."

