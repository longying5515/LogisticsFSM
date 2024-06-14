# 定义JAR包路径
JAR_PATH="target/LogisticsFSM-0.0.1-SNAPSHOT.jar"

# 检查JAR包是否存在
if [ ! -f "$JAR_PATH" ]; then
    echo "Error: JAR file $JAR_PATH not found!"
    exit 1
fi

# 运行JAR包
echo "Running JAR file: $JAR_PATH"
java -jar "$JAR_PATH"
