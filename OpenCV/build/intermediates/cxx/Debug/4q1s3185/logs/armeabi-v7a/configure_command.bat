@echo off
"C:\\Users\\matte\\AppData\\Local\\Android\\Sdk\\cmake\\3.22.1\\bin\\cmake.exe" ^
  "-HC:\\Users\\matte\\AndroidStudioProjects\\livefeed\\OpenCV\\libcxx_helper" ^
  "-DCMAKE_SYSTEM_NAME=Android" ^
  "-DCMAKE_EXPORT_COMPILE_COMMANDS=ON" ^
  "-DCMAKE_SYSTEM_VERSION=24" ^
  "-DANDROID_PLATFORM=android-24" ^
  "-DANDROID_ABI=armeabi-v7a" ^
  "-DCMAKE_ANDROID_ARCH_ABI=armeabi-v7a" ^
  "-DANDROID_NDK=C:\\Users\\matte\\AppData\\Local\\Android\\Sdk\\ndk\\25.1.8937393" ^
  "-DCMAKE_ANDROID_NDK=C:\\Users\\matte\\AppData\\Local\\Android\\Sdk\\ndk\\25.1.8937393" ^
  "-DCMAKE_TOOLCHAIN_FILE=C:\\Users\\matte\\AppData\\Local\\Android\\Sdk\\ndk\\25.1.8937393\\build\\cmake\\android.toolchain.cmake" ^
  "-DCMAKE_MAKE_PROGRAM=C:\\Users\\matte\\AppData\\Local\\Android\\Sdk\\cmake\\3.22.1\\bin\\ninja.exe" ^
  "-DCMAKE_LIBRARY_OUTPUT_DIRECTORY=C:\\Users\\matte\\AndroidStudioProjects\\livefeed\\OpenCV\\build\\intermediates\\cxx\\Debug\\4q1s3185\\obj\\armeabi-v7a" ^
  "-DCMAKE_RUNTIME_OUTPUT_DIRECTORY=C:\\Users\\matte\\AndroidStudioProjects\\livefeed\\OpenCV\\build\\intermediates\\cxx\\Debug\\4q1s3185\\obj\\armeabi-v7a" ^
  "-DCMAKE_BUILD_TYPE=Debug" ^
  "-BC:\\Users\\matte\\AndroidStudioProjects\\livefeed\\OpenCV\\.cxx\\Debug\\4q1s3185\\armeabi-v7a" ^
  -GNinja ^
  "-DANDROID_STL=c++_shared"
