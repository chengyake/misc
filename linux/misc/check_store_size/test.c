#include <sys/statfs.h>
#include <stdio.h>



int check_avail_size()
{   
    int vail_m;

	struct statfs diskInfo;
	
	statfs("/home/chengyake/", &diskInfo);


	vail_m = (diskInfo.f_bavail * diskInfo.f_bsize) >> 20;


    return vail_m;
}



int check_all_size() {
	struct statfs diskInfo;
	
	statfs("/home/chengyake/", &diskInfo);



	unsigned long long blocksize = diskInfo.f_bsize;	//每个block里包含的字节数
	unsigned long long totalsize = blocksize * diskInfo.f_blocks; 	//总的字节数，f_blocks为block的数目
	printf("Total_size = %llu B = %llu KB = %llu MB = %llu GB\n", 
		totalsize, totalsize>>10, totalsize>>20, totalsize>>30);
	
	unsigned long long freeDisk = diskInfo.f_bfree * blocksize;	//剩余空间的大小
	unsigned long long availableDisk = diskInfo.f_bavail * blocksize; 	//可用空间大小
	printf("Disk_free = %llu MB = %llu GB\nDisk_available = %llu MB = %llu GB\n", 
		freeDisk>>20, freeDisk>>30, availableDisk>>20, availableDisk>>30);

}

int main()
{



    printf("%d\n",check_avail_size());
    //check_all_size();





	return 0;
}
