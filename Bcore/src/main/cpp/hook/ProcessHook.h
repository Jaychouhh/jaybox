//
// Created by Milk on 2021/6/6.
//

#ifndef JAYBOX_PROCESSHOOK_H
#define JAYBOX_PROCESSHOOK_H
#include "BaseHook.h"

class ProcessHook : public BaseHook {
public:
    static void init(JNIEnv *env);
};


#endif //JAYBOX_PROCESSHOOK_H
