import tensorflow as tf
import os
import cv2
import math
import random
import numpy as np
import xml.etree.ElementTree as et



HW=256


class Memimages:

    train_list=[]
    val_list=[]

    classes = ["O", "L"]

    def __init__(self, img_dir="./data/", xml_dir="./labels/", outdir="./"):
    
        if os.path.exists(outdir+"train.npz"):
            data=np.load(outdir+"train.npz")
            self.train_list=data['train']
            self.val_list=data['val']
            print("train.npz is already")
            return

        img_list = os.listdir(img_dir)
        img_list.sort()
        xml_list = os.listdir(xml_dir)
        xml_list.sort()
        

        for i in range(len(img_list)):
        #for i in range(1000):
            rate=1.0
            flag=''
            ivl=[]
            vnp=np.ones((64,64,4))*0.001
            data = np.ones((HW,HW,3))*128
            img_np=cv2.imread(img_dir+"/"+img_list[i])
            h,w,c=img_np.shape
            if h < w:
                rate=float(HW)/w
            else:
                rate=float(HW)/h
            
            new_h = int(h*rate/2)*2
            new_w = int(w*rate/2)*2
            img =cv2.resize(img_np, (new_w, new_h))
            #print(data.shape, img.shape, h,w,c,rate)
            data[int(HW/2-new_h/2) : int(HW/2+new_h/2), int(HW/2-new_w/2) : int(HW/2+new_w/2)] = img
            img =data.astype("float32")

            img_np=cv2.imread(xml_dir+"/"+xml_list[i])

            class_id=-1
            if xml_list[i][3]=='h': #H o
                class_id = 0
            if xml_list[i][3]=='s': #V --
                class_id = 1

            if class_id >= 0:
                xsum=np.sum(img_np, axis=0)
                ysum=np.sum(img_np, axis=1)
                yidx = np.where(ysum>0)
                xidx = np.where(xsum>0)
                xmin = float(xidx[0][0])
                xmax = float(xidx[0][-1])
                ymin = float(yidx[0][0])
                ymax = float(yidx[0][-1])
           
                if h < w:
                    ymin=(ymin*rate+(HW-h*rate)/2)/HW
                    ymax=(ymax*rate+(HW-h*rate)/2)/HW
                    xmin=xmin/w
                    xmax=xmax/w
                else:
                    xmin=(xmin*rate+(HW-w*rate)/2)/HW
                    xmax=(xmax*rate+(HW-w*rate)/2)/HW
                    ymin=ymin/h
                    ymax=ymax/h

                center_x = (xmin+xmax)/2
                center_y = (ymin+ymax)/2
                center_w = (xmax-xmin)/2
                center_h = (ymax-ymin)/2

                cyi = int(center_y*HW/4)
                cxi = int(center_x*HW/4)

                vnp[cyi][cxi][class_id]=0.999
                vnp[cyi][cxi][2]=center_w
                vnp[cyi][cxi][3]=center_h
            


            if class_id >=-1:
                ivl.append(img)
                ivl.append(vnp)
                if i%10:
                    self.train_list.append(ivl)
                else:
                    self.val_list.append(ivl)

        #np.savez(outdir+"train.npz", train=self.train_list, val=self.val_list)
        print("make train.npz success")
        print("train:%d  val:%d" % self.get_sum())



    def get_batch(self, size):
        r = random.randint(0,4)
        rdata = random.sample(list(self.train_list), size)
        x=np.array([i[0] for i in rdata])
        y=np.array([i[1] for i in rdata])
        if r == 1:
            x=x[:,::-1,:,:]
            y=y[:,::-1,:,:]
        if r == 2:
            x=x[:,:,::-1,:]
            y=y[:,:,::-1,:]
        if r == 3:
            x=x[:,::-1,::-1,:]
            y=y[:,::-1,::-1,:]

        return x,y
        




    def get_val(self, size):
        rdata = random.sample(list(self.val_list), size)
        x=np.array([i[0] for i in rdata])
        y=np.array([i[1] for i in rdata])
        return x,y

    def get_train_one(self):
        return random.choice(self.train_list)

    def get_val_one(self):
        return random.choice(self.val_list)

    def get_sum(self):
        return len(self.train_list), len(self.val_list)




if __name__ == '__main__':
    test = Memimages()
    print("train:%d  val:%d" % test.get_sum())
#check train datasets
    test_mode=2

    if test_mode==2:
        img,lab = test.get_batch(64)
        #img,lab = test.get_val(64)
        img=img[0].copy()
        img=img
        
        rate = lab[0,:,:,:2]/np.expand_dims(np.sum(lab[0,:,:,:2], axis=-1), axis=-1)
        for i in range(HW/4):
            for j in range(HW/4):
                if np.sum(lab[0,i,j,:2]) >=0.9:
                    index = np.where(rate[i,j,:2]==np.max(rate[i,j,:2]))
                    w = int(lab[0,i,j,2]*HW) 
                    h = int(lab[0,i,j,3]*HW)
                    percent = rate[i,j,index[0][0]]
                    cv2.rectangle(img, (j*4-w, i*4-h), (j*4+w, i*4+h), (0,255,0), 1)
                    cv2.putText(img,test.classes[index[0][0]]+"%0.3f" % percent, (j*4-w,i*4-h),cv2.FONT_HERSHEY_COMPLEX_SMALL,0.8,(0,255,0), 1)
        cv2.imwrite("test_train.png", img)
