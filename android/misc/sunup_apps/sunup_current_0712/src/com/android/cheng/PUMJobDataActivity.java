package com.android.cheng;

import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.text.InputType;
import android.util.Log;
import java.io.IOException;
import android.graphics.Color;
import android.app.Activity;
import android.view.MenuItem;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnCreateContextMenuListener;
import android.view.View.OnFocusChangeListener;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.AdapterView.OnItemClickListener;
import android.content.Context;
import android.content.ServiceConnection;
import android.content.ComponentName;

import android.os.SystemClock;
import com.android.cheng.util.HexDump;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.HashMap;
import android.content.BroadcastReceiver;  

import android.view.ViewGroup;
import android.content.Intent;
import android.content.IntentFilter;  

import android.app.ProgressDialog;
import android.widget.Toast;

import android.content.DialogInterface;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;


import java.io.File;
import android.util.Log;
import jxl.Cell;
import jxl.CellReferenceHelper;
import jxl.CellType;
import jxl.Range;
import jxl.Sheet;
import jxl.Workbook;
import jxl.format.CellFormat;
import jxl.format.Colour;
import jxl.format.UnderlineStyle;
import jxl.read.biff.BiffException;
import jxl.write.Blank;
import jxl.write.DateFormat;
import jxl.write.DateFormats;
import jxl.write.DateTime;
import jxl.write.Formula;
import jxl.write.Label;
import jxl.write.Number;
import jxl.write.NumberFormat;
import jxl.write.WritableCell;
import jxl.write.WritableCellFeatures;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableHyperlink;
import jxl.write.WritableImage;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;



public class PUMJobDataActivity extends Activity {

    private ArrayList<HashMap<String, Object>> listItem = new ArrayList<HashMap<String, Object>>();
    private MyAdapter listItemAdapter;
    private static int i=0;
    private ListView list;
    private IComService mIComService;

	private Button upload;
	private Button download;
	private Button keyA;
	private Button keyB;
	private Button keyC;
	private Button keyD;
	private Button keyE;
	private Button keyF;
	private Button key0;
	private Button key1;
	private Button key2;
	private Button key3;
	private Button key4;
	private Button key5;
	private Button key6;
	private Button key7;
	private Button key8;
	private Button key9;
	private EditText addr;
	private EditText size;
	private Button addfile;

    private String mAddr="";
    private String mSize="";
    private static int currentIF=-1;
    private static boolean keyBusy=false;
    private ProgressDialog dialog; 

    private AddFileThread mAddFileThread = new AddFileThread();

   class AddFileThread extends Thread {

        public void run() {
            String path="/sdcard/PUM500_DATA.xls";

            listItem.clear();
            importExcel(path);


        }

   }


    private ServiceConnection mServiceConnection = new ServiceConnection() {  
        public void onServiceConnected(ComponentName name, IBinder service) {  
            mIComService = IComService.Stub.asInterface(service);
        } 

        public void onServiceDisconnected(ComponentName name) {  
        }  
    };  

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver(){
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(action.equals(ComService.ACTION_UPLOAD_PUM_JOBDATA)){
                String info = intent.getExtras().getString("string"); 
                
                if(info.equals("2E4E4B31040001000100434D524301")) {
                    dialog.cancel(); 

                    new AlertDialog.Builder(PUMJobDataActivity.this)
                        .setTitle("数据请求被拒绝")
                        .setIcon(android.R.drawable.ic_dialog_info)
                        .setPositiveButton("确定", null)
                        .show();

                    return ;
                }

                int a,i;
                a = HexDump.hexStringToInt(mAddr, 4);
                listItemAdapter.setStartAddr(a);

                StringBuffer subBuf=new StringBuffer();

                subBuf.append(info);

                //Log.e("chengyake", "aaaaaaaaaaaaa" + HexDump.hexStringToInt(mSize, 2) + " addr " + mAddr + " long addr " + HexDump.toHexString(a));
                
                
                //listItem.clear();
                //for(i=20; i<(2*HexDump.hexStringToInt(mSize, 2)+20); i+=2) {
                for(i=(4*HexDump.hexStringToInt(mSize, 2)+16); i>=20; i-=4) {
                   HashMap<String, Object> map = new HashMap<String, Object>();
                   map.put("ItemAddr", HexDump.toHexString(a+(i-20)/4));
                   map.put("ItemOld", subBuf.substring(i, 4+i));
                   map.put("ItemNew", "");
                   listItem.add(0, map);
                }
                listItemAdapter.notifyDataSetChanged();

                Toast.makeText(getApplicationContext(), "上传成功",Toast.LENGTH_SHORT).show();
                dialog.cancel(); 
            } else if(action.equals(ComService.ACTION_FLASH_PUM_JOBDATA)) {
                dialog.cancel(); 
            } else if (action.equals(ComService.ACTION_FINISH)) {
                Log.e("chengyake", "in PUMJobDataActivity finish");
                finish();
            }
        }

    };

    private class MyAdapter extends SimpleAdapter {
        int currentPos = -1;
        int startPos = -1;
        int count = 0;
        private ArrayList<HashMap<String, Object>> mItemList;
        public MyAdapter(Context context, ArrayList<HashMap<String, Object>> data,
                int resource, String[] from, int[] to) {
            super(context, data, resource, from, to);
            mItemList = (ArrayList<HashMap<String, Object>>) data;
            if(data == null) {
                count = 0;
            } else {
                count = data.size();
            }
        }

        public void setCurrent(int pos) {
            currentPos = pos;
        }
        public void setStartAddr(int start) {
            startPos = start;
        }
        public int getCount() {
            return mItemList.size();
        }

        public Object getItem(int pos) {
            return pos;
        }

        public long getItemId(int pos) {
            return pos;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = super.getView(position, convertView, parent);
            TextView mNew = (TextView)view.findViewById(R.id.ItemNew);
            TextView mOld = (TextView)view.findViewById(R.id.ItemOld);
            TextView mAdd = (TextView)view.findViewById(R.id.ItemAddr);

            mNew.setTextColor(Color.GRAY);
            mOld.setTextColor(Color.GRAY);
            mAdd.setTextColor(Color.GRAY);

            if((!mNew.getText().toString().equals("")) && 
                    (!mNew.getText().toString().equals(mOld.getText().toString()))) {

                mNew.setTextColor(Color.RED);
                mOld.setTextColor(Color.RED);
                mAdd.setTextColor(Color.RED);

            }
           if(currentPos == HexDump.hexStringToInt(mAdd.getText().toString(), 4) - startPos)  {
               Log.e("chengyake", "-------------start: " + startPos + " current: " + currentPos + " text: " + HexDump.hexStringToInt(mAdd.getText().toString(), 4));
                mNew.setTextColor(Color.GREEN);
                mOld.setTextColor(Color.GREEN);
                mAdd.setTextColor(Color.GREEN);
            }
            return view;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pus_job_data);

		IntentFilter myIntentFilter = new IntentFilter();
		myIntentFilter.addAction(ComService.ACTION_UPLOAD_PUM_JOBDATA);
		myIntentFilter.addAction(ComService.ACTION_FLASH_PUM_JOBDATA);
		myIntentFilter.addAction(ComService.ACTION_FINISH);
		registerReceiver(mBroadcastReceiver, myIntentFilter);


        list = (ListView) findViewById(R.id.pus_job_data_listview);
        upload=(Button)findViewById(R.id.pus_job_data_upload);
        addfile=(Button)findViewById(R.id.add_file);
        download=(Button)findViewById(R.id.pus_job_data_download);
        keyA=(Button)findViewById(R.id.pus_job_data_keyA);
        keyB=(Button)findViewById(R.id.pus_job_data_keyB);
        keyC=(Button)findViewById(R.id.pus_job_data_keyC);
        keyD=(Button)findViewById(R.id.pus_job_data_keyD);
        keyE=(Button)findViewById(R.id.pus_job_data_keyE);
        keyF=(Button)findViewById(R.id.pus_job_data_keyF);
        key0=(Button)findViewById(R.id.pus_job_data_key0);
        key1=(Button)findViewById(R.id.pus_job_data_key1);
        key2=(Button)findViewById(R.id.pus_job_data_key2);
        key3=(Button)findViewById(R.id.pus_job_data_key3);
        key4=(Button)findViewById(R.id.pus_job_data_key4);
        key5=(Button)findViewById(R.id.pus_job_data_key5);
        key6=(Button)findViewById(R.id.pus_job_data_key6);
        key7=(Button)findViewById(R.id.pus_job_data_key7);
        key8=(Button)findViewById(R.id.pus_job_data_key8);
        key9=(Button)findViewById(R.id.pus_job_data_key9);

        addr=(EditText)findViewById(R.id.pus_job_data_addr);
        size=(EditText)findViewById(R.id.pus_job_data_size);

        addr.setInputType(InputType.TYPE_NULL);
        size.setInputType(InputType.TYPE_NULL);

        addr.setOnFocusChangeListener(new View.OnFocusChangeListener() { 
           @Override 
           public void onFocusChange(View v, boolean hasFocus) { 
                if(hasFocus){ 
                    addr.setBackgroundColor(Color.GREEN);
                    currentIF=-1;
                    listItemAdapter.setCurrent(currentIF);
                    listItemAdapter.notifyDataSetChanged();
                } else {
                    addr.setBackgroundColor(Color.WHITE);
                } 
           } 
        });

        size.setOnFocusChangeListener(new View.OnFocusChangeListener() { 
           @Override 
           public void onFocusChange(View v, boolean hasFocus) { 
                if(hasFocus){ 
                    size.setBackgroundColor(Color.GREEN);
                    currentIF=-1;
                    listItemAdapter.setCurrent(currentIF);
                    listItemAdapter.notifyDataSetChanged();
                } else {
                    size.setBackgroundColor(Color.WHITE);
                } 
           } 
        });

        addfile.setOnClickListener(new View.OnClickListener() {
            @Override 
			public void onClick(View v) { 

                //mAddFileThread.start();
             
			}
		});

        upload.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) { 


                dialog = new ProgressDialog(PUMJobDataActivity.this); 
                dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER); 
                dialog.setTitle("PUM"); 
                dialog.setMessage("正在上传数据..."); 
                dialog.setIcon(android.R.drawable.ic_dialog_map); 
                dialog.setIndeterminate(false); 
                dialog.setCancelable(false);
                dialog.show();   

                upload();

			}
		});

        download.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) { 
         
            new AlertDialog.Builder(PUMJobDataActivity.this)
            .setTitle("确定下载？")
            .setIcon(android.R.drawable.ic_dialog_info)
            .setPositiveButton("确定", new DialogInterface.OnClickListener() { 
                @Override  
                public void onClick(DialogInterface dia, int which) {  

                    dialog = new ProgressDialog(PUMJobDataActivity.this); 
                    dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER); 
                    dialog.setTitle("PUM"); 
                    dialog.setMessage("正在下载数据..."); 
                    dialog.setIcon(android.R.drawable.ic_dialog_map); 
                    dialog.setIndeterminate(false); 
                    dialog.setCancelable(false);
                    dialog.show();    
                    
                    download();
                }
            })
            .setNegativeButton("取消", null)
            .show();

            }
		});

		//key 0 - key 9 key A- key F
        keyA.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) { 
               try {
                   if(addr.hasFocus() && mAddr.length()<8 ) {
                       mAddr=mAddr + "A";
                       addr.setText(mAddr);
                   } else if(size.hasFocus() && mSize.length()<4) {
                       mSize=mSize + "A";
                       size.setText(mSize);
                   } else if(currentIF>=0) {
                       HashMap<String, Object> map = listItem.remove(currentIF);
                       String check = (String) map.get("ItemNew");
                       if(check.length()<4) {
                            map.put("ItemNew", check + "A");
                       }
                       listItem.add(currentIF, map);
                       listItemAdapter.notifyDataSetChanged();
                   }
               } catch  (Exception e) {
                   throw new IllegalStateException("keyA error", e);
               }
			}
		});

        keyB.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) { 
               try {
                   if(addr.hasFocus() && mAddr.length()<8 ) {
                       mAddr=mAddr + "B";
                       addr.setText(mAddr);
                   }
                   if(size.hasFocus() && mSize.length()<4) {
                       mSize=mSize + "B";
                       size.setText(mSize);
                   } else if(currentIF>=0) {
                       HashMap<String, Object> map = listItem.remove(currentIF);
                       String check = (String) map.get("ItemNew");
                       if(check.length()<4) {
                            map.put("ItemNew", check + "B");
                       }
                       listItem.add(currentIF, map);
                       listItemAdapter.notifyDataSetChanged();
                   }

               } catch  (Exception e) {
                   throw new IllegalStateException("keyB error", e);
               }
			}
		});

        keyC.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) { 
               try {
                   if(addr.hasFocus() && mAddr.length()<8 ) {
                       mAddr=mAddr + "C";
                       addr.setText(mAddr);
                   }
                   if(size.hasFocus() && mSize.length()<4) {
                       mSize=mSize + "C";
                       size.setText(mSize);
                   } else if(currentIF>=0) {
                       HashMap<String, Object> map = listItem.remove(currentIF);
                       String check = (String) map.get("ItemNew");
                       if(check.length()<4) {
                            map.put("ItemNew", check + "C");
                       }
                       listItem.add(currentIF, map);
                       listItemAdapter.notifyDataSetChanged();
                   }

               } catch  (Exception e) {
                   throw new IllegalStateException("keyC error", e);
               }
			}
		});

        keyD.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) { 
               try {
                   if(addr.hasFocus() && mAddr.length()<8 ) {
                       mAddr=mAddr + "D";
                       addr.setText(mAddr);
                   }
                   if(size.hasFocus() && mSize.length()<4) {
                       mSize=mSize + "D";
                       size.setText(mSize);
                   } else if(currentIF>=0) {
                       HashMap<String, Object> map = listItem.remove(currentIF);
                       String check = (String) map.get("ItemNew");
                       if(check.length()<4) {
                            map.put("ItemNew", check + "D");
                       }
                       listItem.add(currentIF, map);
                       listItemAdapter.notifyDataSetChanged();
                   }

               } catch  (Exception e) {
                   throw new IllegalStateException("keyA error", e);
               }
			}
		});

        keyE.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) { 
               try {
                   if(addr.hasFocus() && mAddr.length()<8 ) {
                       mAddr=mAddr + "E";
                       addr.setText(mAddr);
                   }
                   if(size.hasFocus() && mSize.length()<4) {
                       mSize=mSize + "E";
                       size.setText(mSize);
                   } else if(currentIF>=0) {
                       HashMap<String, Object> map = listItem.remove(currentIF);
                       String check = (String) map.get("ItemNew");
                       if(check.length()<4) {
                            map.put("ItemNew", check + "E");
                       }
                       listItem.add(currentIF, map);
                       listItemAdapter.notifyDataSetChanged();
                   }

               } catch  (Exception e) {
                   throw new IllegalStateException("keyA error", e);
               }
			}
		});

        keyF.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) { 
               try {
                   if(addr.hasFocus() && mAddr.length()<8 ) {
                       mAddr=mAddr + "F";
                       addr.setText(mAddr);
                   }
                   if(size.hasFocus() && mSize.length()<4) {
                       mSize=mSize + "F";
                       size.setText(mSize);
                   } else if(currentIF>=0) {
                       Log.e("chengyake", "........................");
                       HashMap<String, Object> map = listItem.remove(currentIF);
                       String check = (String) map.get("ItemNew");
                       if(check.length()<4) {
                            map.put("ItemNew", check + "F");
                       }
                       listItem.add(currentIF, map);
                       listItemAdapter.notifyDataSetChanged();
                   }

               } catch  (Exception e) {
                   throw new IllegalStateException("keyA error", e);
               }
			}
		});

        key0.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) { 
               try {
                   if(addr.hasFocus() && mAddr.length()<8 ) {
                       mAddr=mAddr + "0";
                       addr.setText(mAddr);
                   }
                   if(size.hasFocus() && mSize.length()<4) {
                       mSize=mSize + "0";
                       size.setText(mSize);
                   } else if(currentIF>=0) {
                       HashMap<String, Object> map = listItem.remove(currentIF);
                       String check = (String) map.get("ItemNew");
                       if(check.length()<4) {
                            map.put("ItemNew", check + "0");
                       }
                       listItem.add(currentIF, map);
                       listItemAdapter.notifyDataSetChanged();
                   }

               } catch  (Exception e) {
                   throw new IllegalStateException("key0 error", e);
               }
			}
		});

        key0.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
			public boolean onLongClick(View v) { 
               try {
                   if(addr.hasFocus()) {
                       mAddr="";
                       addr.setText("0");
                   }
                   if(size.hasFocus()) {
                       mSize="";
                       size.setText("0");
                   } else if(currentIF>=0) {
                       HashMap<String, Object> map = listItem.remove(currentIF);
                       map.put("ItemNew", "");
                       listItem.add(currentIF, map);
                       listItemAdapter.notifyDataSetChanged();
                       }
               } catch  (Exception e) {
                   throw new IllegalStateException("key0 error", e);
               }

               return true;
			}
		});

        key1.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) { 
               try {
                   if(addr.hasFocus() && mAddr.length()<8 ) {
                       mAddr=mAddr + "1";
                       addr.setText(mAddr);
                   }
                   if(size.hasFocus() && mSize.length()<4) {
                       mSize=mSize + "1";
                       size.setText(mSize);
                   } else if(currentIF>=0) {
                       HashMap<String, Object> map = listItem.remove(currentIF);
                       String check = (String) map.get("ItemNew");
                       if(check.length()<4) {
                            map.put("ItemNew", check + "1");
                       }
                       listItem.add(currentIF, map);
                       listItemAdapter.notifyDataSetChanged();
                   }

               } catch  (Exception e) {
                   throw new IllegalStateException("key1 error", e);
               }
			}
		});

        key2.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) { 
               try {
                   if(addr.hasFocus() && mAddr.length()<8 ) {
                       mAddr=mAddr + "2";
                       addr.setText(mAddr);
                   }
                   if(size.hasFocus() && mSize.length()<4) {
                       mSize=mSize + "2";
                       size.setText(mSize);
                   } else if(currentIF>=0) {
                       HashMap<String, Object> map = listItem.remove(currentIF);
                       String check = (String) map.get("ItemNew");
                       if(check.length()<4) {
                            map.put("ItemNew", check + "2");
                       }
                       listItem.add(currentIF, map);
                       listItemAdapter.notifyDataSetChanged();
                   }

               } catch  (Exception e) {
                   throw new IllegalStateException("key2 error", e);
               }
			}
		});

        key3.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) { 
               try {
                   if(addr.hasFocus() && mAddr.length()<8 ) {
                       mAddr=mAddr + "3";
                       addr.setText(mAddr);
                   }
                   if(size.hasFocus() && mSize.length()<4) {
                       mSize=mSize + "3";
                       size.setText(mSize);
                   } else if(currentIF>=0) {
                       HashMap<String, Object> map = listItem.remove(currentIF);
                       String check = (String) map.get("ItemNew");
                       if(check.length()<4) {
                            map.put("ItemNew", check + "3");
                       }
                       listItem.add(currentIF, map);
                       listItemAdapter.notifyDataSetChanged();
                   }

               } catch  (Exception e) {
                   throw new IllegalStateException("key3 error", e);
               }
			}
		});
        key4.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) { 
               try {
                   if(addr.hasFocus() && mAddr.length()<8 ) {
                       mAddr=mAddr + "4";
                       addr.setText(mAddr);
                   }
                   if(size.hasFocus() && mSize.length()<4) {
                       mSize=mSize + "4";
                       size.setText(mSize);
                   } else if(currentIF>=0) {
                       HashMap<String, Object> map = listItem.remove(currentIF);
                       String check = (String) map.get("ItemNew");
                       if(check.length()<4) {
                            map.put("ItemNew", check + "4");
                       }
                       listItem.add(currentIF, map);
                       listItemAdapter.notifyDataSetChanged();
                   }

               } catch  (Exception e) {
                   throw new IllegalStateException("key4 error", e);
               }
			}
		});

        key5.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) { 
               try {
                   if(addr.hasFocus() && mAddr.length()<8 ) {
                       mAddr=mAddr + "5";
                       addr.setText(mAddr);
                   }
                   if(size.hasFocus() && mSize.length()<4) {
                       mSize=mSize + "5";
                       size.setText(mSize);
                   } else if(currentIF>=0) {
                       HashMap<String, Object> map = listItem.remove(currentIF);
                       String check = (String) map.get("ItemNew");
                       if(check.length()<4) {
                            map.put("ItemNew", check + "5");
                       }
                       listItem.add(currentIF, map);
                       listItemAdapter.notifyDataSetChanged();
                   }

               } catch  (Exception e) {
                   throw new IllegalStateException("key5 error", e);
               }
			}
		});

        key6.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) { 
               try {
                   if(addr.hasFocus() && mAddr.length()<8 ) {
                       mAddr=mAddr + "6";
                       addr.setText(mAddr);
                   }
                   if(size.hasFocus() && mSize.length()<4) {
                       mSize=mSize + "6";
                       size.setText(mSize);
                   } else if(currentIF>=0) {
                       HashMap<String, Object> map = listItem.remove(currentIF);
                       String check = (String) map.get("ItemNew");
                       if(check.length()<4) {
                            map.put("ItemNew", check + "6");
                       }
                       listItem.add(currentIF, map);
                       listItemAdapter.notifyDataSetChanged();
                   }

               } catch  (Exception e) {
                   throw new IllegalStateException("key6 error", e);
               }
			}
		});

        key7.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) { 
               try {
                   if(addr.hasFocus() && mAddr.length()<8 ) {
                       mAddr=mAddr + "7";
                       addr.setText(mAddr);
                   }
                   if(size.hasFocus() && mSize.length()<4) {
                       mSize=mSize + "7";
                       size.setText(mSize);
                   } else if(currentIF>=0) {
                       HashMap<String, Object> map = listItem.remove(currentIF);
                       String check = (String) map.get("ItemNew");
                       if(check.length()<4) {
                            map.put("ItemNew", check + "7");
                       }
                       listItem.add(currentIF, map);
                       listItemAdapter.notifyDataSetChanged();
                   }

               } catch  (Exception e) {
                   throw new IllegalStateException("key7 error", e);
               }
			}
		});

        key8.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) { 
               try {
                   if(addr.hasFocus() && mAddr.length()<8 ) {
                       mAddr=mAddr + "8";
                       addr.setText(mAddr);
                   }
                   if(size.hasFocus() && mSize.length()<4) {
                       mSize=mSize + "8";
                       size.setText(mSize);
                   } else if(currentIF>=0) {
                       HashMap<String, Object> map = listItem.remove(currentIF);
                       String check = (String) map.get("ItemNew");
                       if(check.length()<4) {
                            map.put("ItemNew", check + "8");
                       }
                       listItem.add(currentIF, map);
                       listItemAdapter.notifyDataSetChanged();
                   }

               } catch  (Exception e) {
                   throw new IllegalStateException("key8 error", e);
               }
			}
		});

        key9.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) { 
               try {
                   if(addr.hasFocus() && mAddr.length()<8 ) {
                       mAddr=mAddr + "9";
                       addr.setText(mAddr);
                   }
                   if(size.hasFocus() && mSize.length()<4) {
                       mSize=mSize + "9";
                       size.setText(mSize);
                   } else if(currentIF>=0) {
                       HashMap<String, Object> map = listItem.remove(currentIF);
                       String check = (String) map.get("ItemNew");
                       if(check.length()<4) {
                            map.put("ItemNew", check + "9");
                       }
                       listItem.add(currentIF, map);
                       listItemAdapter.notifyDataSetChanged();
                   }

               } catch  (Exception e) {
                   throw new IllegalStateException("key9 error", e);
               }
			}
		});


        listItemAdapter = new MyAdapter(this,listItem, 
                R.layout.pus_job_data_item,
                new String[] {"ItemAddr","ItemOld", "ItemNew"}, 
                new int[] {R.id.ItemAddr,R.id.ItemOld,R.id.ItemNew}
                );

        list.setAdapter(listItemAdapter);

        list.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int arg2, long arg3) {
            //setTitle("点击第"+arg2+"个项目");
            addr.clearFocus();
            size.clearFocus();
            currentIF=arg2;
            listItemAdapter.setCurrent(currentIF);
            listItemAdapter.notifyDataSetChanged();
            //listItemAdapter.setCurrent(0);
            }

        });

        list.setOnCreateContextMenuListener(new OnCreateContextMenuListener() {
            @Override
            public void onCreateContextMenu(ContextMenu menu, View v,ContextMenuInfo menuInfo) {
                //menu.setHeaderTitle("长按菜单-ContextMenu");   
                /*
                HashMap<String, Object> map = new HashMap<String, Object>();
                map.put("ItemAddr", "add");
                map.put("ItemOld", "3");
                map.put("ItemNew", "");
                listItem.add(map);
                listItemAdapter.mItemList = listItem;
                */
                listItemAdapter.notifyDataSetChanged();
            }
        }); 



        Intent j  = new Intent();  
        j.setClass(PUMJobDataActivity.this, ComService.class);
        getApplicationContext().bindService(j, mServiceConnection, BIND_AUTO_CREATE);

        Log.e("chengyake", "in PUMJobDataActivity onCreate");
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        //setTitle("长按"+item.getItemId()); 

        
        return super.onContextItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        Log.e("chengyake", "Override in PUMJobDataActivity onDestroy func");
        getApplicationContext().unbindService(mServiceConnection);
        this.unregisterReceiver(mBroadcastReceiver);
        super.onDestroy();
    }

    private void upload() {

        int i;
        if(keyBusy == true || mAddr == null || mSize == null || mAddr.equals("") || mSize.equals("") ) {
            dialog.cancel(); 
            Toast.makeText(getApplicationContext(), "addr or size is null; or device is busy", Toast.LENGTH_SHORT).show();
            return ;
        }

        keyBusy = true;
        StringBuffer addrBuf=new StringBuffer(); 
        StringBuffer sizeBuf=new StringBuffer();

        int addrLen = mAddr.length();
        int sizeLen = mSize.length();

        for(i=0; i<8-addrLen; i++) {
            addrBuf.append("0");
        }
        addrBuf.append(mAddr);

        for(i=0; i<4-sizeLen; i++) {
            sizeBuf.append("0");
        }
        sizeBuf.append(mSize);

        /*
           mAddr = addrBuf.substring(6, 8) + addrBuf.substring(4, 6) +
           addrBuf.substring(2, 4) + addrBuf.substring(0, 2);
           mSize = sizeBuf.substring(2, 4) + sizeBuf.substring(0, 2);
         */

        mAddr = addrBuf.substring(0, 8);
        mSize = sizeBuf.substring(0, 4);

        listItem.clear();

        try {
            mIComService.uploadPUMJobdata(mAddr, mSize);
        } catch  (Exception e) {
            throw new IllegalStateException("upload error", e);
        }
        keyBusy = false;

        }



    private void download() {
            if(keyBusy == true || mAddr == null || mSize == null || mAddr.equals("") || mSize.equals("") ) {
                dialog.cancel(); 
                Toast.makeText(getApplicationContext(), "addr or size is null; or device is busy", Toast.LENGTH_SHORT).show();
                return ;
            }

            if(HexDump.hexStringToInt(mAddr, 4) < 0x8500 || HexDump.hexStringToInt(mAddr, 4) > 0x8eff) {
                dialog.cancel(); 
                Toast.makeText(getApplicationContext(), "Address is protected",Toast.LENGTH_SHORT).show();
                return;
            }

            keyBusy = true;
            //pre download
            try {
                 while(-1 == mIComService.preDownloadPUMJobdata());
            } catch  (Exception e) {
                 throw new IllegalStateException("upload error", e);
            }

            //write into mem
            for(int j=0; j<listItemAdapter.getCount(); j++) {
                HashMap<String, Object> map = listItem.remove(j);
                String checkNew = (String) map.get("ItemNew");
                String checkOld = (String) map.get("ItemOld");
                if(!checkNew.equals("") && !checkOld.equals(checkNew)) {
                    try {
                         while(-1 == mIComService.downloadPUMJobdata((String)map.get("ItemAddr"), checkNew));
                    } catch  (Exception e) {
                         throw new IllegalStateException("upload error", e);
                    }
                }
                listItem.add(j, map);
                listItemAdapter.notifyDataSetChanged();
			}

            //flash into flash
            try {
                 while(-1 == mIComService.flashPUMJobdata());
            } catch  (Exception e) {
                 throw new IllegalStateException("upload error", e);
            }

            keyBusy = false;


    }
 
    private String importExcel(String file) {

        File inputWorkbook;
        try {
            inputWorkbook = new File(file);
            Workbook book = Workbook.getWorkbook(inputWorkbook);
            int num = book.getNumberOfSheets();  
            Sheet sheet = book.getSheet(0);
            int Rows = sheet.getRows();  


            for (int j = 0; j < Rows; j++) {
                String addr = sheet.getCell(0, j).getContents();
                String newv = sheet.getCell(1, j).getContents();
                String size = sheet.getCell(2, j).getContents();
                if(addr.contains(";")) {
                    continue;
                }

                Log.e("chengyake", "............." + addr + " "  +  newv + " " + size);
                //fenge
                StringBuffer addrBuf=new StringBuffer(); 
                addrBuf.append(addr);
                addr = addrBuf.substring(2, 10);

                mAddr = addr;
                mSize = size;
                do{

                    Log.e("chengyake", "......................................0");
                    dialog = new ProgressDialog(PUMJobDataActivity.this); 
                    dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER); 
                    dialog.setTitle("PUM"); 
                    dialog.setMessage("正在上传数据..."); 
                    dialog.setIcon(android.R.drawable.ic_dialog_map); 
                    dialog.setIndeterminate(false); 
                    dialog.setCancelable(false);
                    dialog.show();   
                    SystemClock.sleep(10);
                } while(0!=uploadInv());
                    SystemClock.sleep(1000);

                /*
                Log.e("chengyake", "......................................1");
                HashMap<String, Object> map = listItem.remove(0);
                Log.e("chengyake", "......................................2");
                map.put("ItemNew", newv);
                Log.e("chengyake", "......................................3");
                listItem.add(0, map);
                Log.e("chengyake", "......................................4");
                listItemAdapter.notifyDataSetChanged();
                Log.e("chengyake", "......................................5");
                */

            }
            book.close();  
        } catch (Exception e) {
            System.out.println(e);  
            return "文件/sdcard/CONSOLE_ERRORCODE.xls不存在";
        }

        return "chengyake";
    }

    private int uploadInv() {

        int i, ret;
        if(keyBusy == true || mAddr == null || mSize == null || mAddr.equals("") || mSize.equals("") ) {
            dialog.cancel(); 
            Toast.makeText(getApplicationContext(), "addr or size is null; or device is busy", Toast.LENGTH_SHORT).show();
            return -1;
        }
        
        Log.e("chengyake", "............." + mAddr + " "  +  mSize );

        keyBusy = true;
        StringBuffer addrBuf=new StringBuffer(); 
        StringBuffer sizeBuf=new StringBuffer();

        int addrLen = mAddr.length();
        int sizeLen = mSize.length();

        for(i=0; i<8-addrLen; i++) {
            addrBuf.append("0");
        }
        addrBuf.append(mAddr);

        for(i=0; i<4-sizeLen; i++) {
            sizeBuf.append("0");
        }
        sizeBuf.append(mSize);

        /*
           mAddr = addrBuf.substring(6, 8) + addrBuf.substring(4, 6) +
           addrBuf.substring(2, 4) + addrBuf.substring(0, 2);
           mSize = sizeBuf.substring(2, 4) + sizeBuf.substring(0, 2);
         */

        mAddr = addrBuf.substring(0, 8);
        mSize = sizeBuf.substring(0, 4);

        listItem.clear();

        try {
            Log.e("chengyake", "............." + mAddr + " "  +  mSize );
            ret = mIComService.uploadPUMJobdata(mAddr, mSize);
        } catch  (Exception e) {
            throw new IllegalStateException("upload error", e);
        }
        keyBusy = false;
        
        return ret;
        }


}

