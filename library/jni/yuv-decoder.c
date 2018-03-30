#include <jni.h>
#include <android/log.h>
#include <stdlib.h>

JNIEXPORT void JNICALL Java_jp_co_cyberagent_android_gpuimage_GPUImageNativeLibrary_YUVtoRBGA(JNIEnv * env, jobject obj, jbyteArray yuv420sp, jint width, jint height, jintArray rgbOut)
{
    float           w0=0.6;
    float           t0=0.1;
    float           percent;
    float           temp;
    int             under_50=0;

    int             sz;
    int             i;
    int             j;
    int             Y;
    int             Cr = 0;
    int             Cb = 0;
    int             pixPtr = 0;
    int             jDiv2 = 0;
    int             R = 0;
    int             G = 0;
    int             B = 0;
    int             cOff;
    int w = width;
    int h = height;
    int             total=w*h*3;
    int             Max_dark_channel=0;
    sz = w * h;
    float *t=(float  *)malloc(sizeof(float)*sz);
    float *dark_I=(float  *)malloc(sizeof(float)*sz);
    float *gray =(float *)malloc(sizeof(float)*sz);
    int *r=(int   *)malloc(sizeof(int )*sz);
    int *g=(int   *)malloc(sizeof(int )*sz);
    int *b=(int   *)malloc(sizeof(int )*sz);
    jint *rgbData = (jint*) ((*env)->GetPrimitiveArrayCritical(env, rgbOut, 0));
    jbyte* yuv = (jbyte*) (*env)->GetPrimitiveArrayCritical(env, yuv420sp, 0);

    for(j = 0; j < h; j++) {
             pixPtr = j * w;
             jDiv2 = j >> 1;
             for(i = 0; i < w; i++,pixPtr++) {
                     Y = yuv[pixPtr];
                     if(Y < 0) Y += 255;
                     if((i & 0x1) != 1) {
                             cOff = sz + jDiv2 * w + (i >> 1) * 2;
                             Cb = yuv[cOff];
                             if(Cb < 0) Cb += 127; else Cb -= 128;
                             Cr = yuv[cOff + 1];
                             if(Cr < 0) Cr += 127; else Cr -= 128;
                     }

                     //ITU-R BT.601 conversion
                     //
                     //R = 1.164*(Y-16) + 2.018*(Cr-128);
                     //G = 1.164*(Y-16) - 0.813*(Cb-128) - 0.391*(Cr-128);
                     //B = 1.164*(Y-16) + 1.596*(Cb-128);
                     //
                     Y = Y + (Y >> 3) + (Y >> 5) + (Y >> 7);
                     R = Y + (Cr << 1) + (Cr >> 6);
                     if(R < 0) R = 0; else if(R > 255) R = 255;
                     G = Y - Cb + (Cb >> 3) + (Cb >> 4) - (Cr >> 1) + (Cr >> 3);
                     if(G < 0) G = 0; else if(G > 255) G = 255;
                     B = Y + Cb + (Cb >> 1) + (Cb >> 4) + (Cb >> 5);
                     if(B < 0) B = 0; else if(B > 255) B = 255;
                     gray[pixPtr]=0.2989*R+ 0.587*G + 0.114*B;
                     r[pixPtr]=R;
                     g[pixPtr]=G;
                     b[pixPtr]=B;
                     if(G>B)
                     {
                         dark_I[pixPtr]=B;
                         if(R<B)
                            dark_I[pixPtr]=R;
                     }
                     else
                     {
                         dark_I[pixPtr]=G;
                         if(R<G)
                            dark_I[pixPtr]=R;
                     }
                     if(dark_I[pixPtr]>Max_dark_channel)
                        Max_dark_channel=dark_I[pixPtr];
                     if(gray[pixPtr]<=50)
                        under_50++;
             }
    }
    percent=under_50/total;
    if(percent>0.02)
        w0=0.8;
    else if (percent>0.01)
        w0=0.3;
    else if (percent>0.003)
        w0=0.45;
    else if (percent>0.0001)
        w0=0.55;
    else if (percent>0)
        w0=0.8;
    else if (percent==0)
        w0=0.7;
    for(j = 0; j < h; j++) {
            pixPtr = j * w;
        for(i = 0; i < w; i++,pixPtr++){
            temp=1-w0*(dark_I[pixPtr]/Max_dark_channel);
            t[pixPtr]=temp>t0?temp:t0;
            temp=(1-t[pixPtr])*Max_dark_channel;
            R=(r[pixPtr]-temp)/t[pixPtr];
            G=(g[pixPtr]-temp)/t[pixPtr];
            B=(b[pixPtr]-temp)/t[pixPtr];
            rgbData[pixPtr] = 0xff000000 + (R << 16) + (G << 8) + B;
        }
    }
    free(t);
    free(dark_I);
    free(gray);
    free(r);
    free(g);
    free(b);
    (*env)->ReleasePrimitiveArrayCritical(env, rgbOut, rgbData, 0);
    (*env)->ReleasePrimitiveArrayCritical(env, yuv420sp, yuv, 0);
}
JNIEXPORT void JNICALL Java_jp_co_cyberagent_android_gpuimage_GPUImageNativeLibrary_YUVtoARBG(JNIEnv * env, jobject obj, jbyteArray yuv420sp, jint width, jint height, jintArray rgbOut)
{
    int             sz;
    int             i;
    int             j;
    int             Y;
    int             Cr = 0;
    int             Cb = 0;
    int             pixPtr = 0;
    int             jDiv2 = 0;
    int             R = 0;
    int             G = 0;
    int             B = 0;
    int             cOff;
    int w = width;
    int h = height;
    sz = w * h;

    jint *rgbData = (jint*) ((*env)->GetPrimitiveArrayCritical(env, rgbOut, 0));
    jbyte* yuv = (jbyte*) (*env)->GetPrimitiveArrayCritical(env, yuv420sp, 0);

    for(j = 0; j < h; j++) {
             pixPtr = j * w;
             jDiv2 = j >> 1;
             for(i = 0; i < w; i++) {
                     Y = yuv[pixPtr];
                     if(Y < 0) Y += 255;
                     if((i & 0x1) != 1) {
                             cOff = sz + jDiv2 * w + (i >> 1) * 2;
                             Cb = yuv[cOff];
                             if(Cb < 0) Cb += 127; else Cb -= 128;
                             Cr = yuv[cOff + 1];
                             if(Cr < 0) Cr += 127; else Cr -= 128;
                     }

                     //ITU-R BT.601 conversion
                     //
                     //R = 1.164*(Y-16) + 2.018*(Cr-128);
                     //G = 1.164*(Y-16) - 0.813*(Cb-128) - 0.391*(Cr-128);
                     //B = 1.164*(Y-16) + 1.596*(Cb-128);
                     //
                     Y = Y + (Y >> 3) + (Y >> 5) + (Y >> 7);
                     R = Y + (Cr << 1) + (Cr >> 6);
                     if(R < 0) R = 0; else if(R > 255) R = 255;
                     G = Y - Cb + (Cb >> 3) + (Cb >> 4) - (Cr >> 1) + (Cr >> 3);
                     if(G < 0) G = 0; else if(G > 255) G = 255;
                     B = Y + Cb + (Cb >> 1) + (Cb >> 4) + (Cb >> 5);
                     if(B < 0) B = 0; else if(B > 255) B = 255;
                     rgbData[pixPtr++] = 0xff000000 + (R << 16) + (G << 8) + B;
             }
    }

    (*env)->ReleasePrimitiveArrayCritical(env, rgbOut, rgbData, 0);
    (*env)->ReleasePrimitiveArrayCritical(env, yuv420sp, yuv, 0);
}
