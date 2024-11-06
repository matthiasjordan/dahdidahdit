cmake_minimum_required(VERSION 3.10.2)
project(Dahdidahdit)

set(CMAKE_C_FLAGS "${CMAKE_C_FLAGS} -std=c99 -Wall")
add_link_options(LINKER:--build-id=none)

add_library(native-audio-jni SHARED
            native-audio-jni.c)

# Include libraries needed for native-audio-jni lib
target_link_libraries(native-audio-jni
                      android
                      log
                      OpenSLES)
