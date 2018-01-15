- skip test
  1. $ gradle build -x test
  2. $  
- refresh dependencies
  1. $ 
  ```
  rm -rf /home/administrator/.gradle/caches/modules-2/files-2.1/com.github.conanchen.gedit-api-grpc/
  ```
  2. $ gradle build -x test --refresh-dependencies