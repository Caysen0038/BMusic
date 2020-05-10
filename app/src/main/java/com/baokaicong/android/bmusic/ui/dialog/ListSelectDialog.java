package com.baokaicong.android.bmusic.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.baokaicong.android.bmusic.R;
import com.baokaicong.android.bmusic.ui.adapter.MenuListAdapter;
import com.baokaicong.android.bmusic.ui.view.ListRefreshFooterView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ListSelectDialog extends Dialog{
    private ListView listView;
    private Context context;
    private View root;
    private TextView titleView;
    private ListSelectAdapter adapter;
    private Map<Integer, View.OnClickListener> listenerMap;


    public ListSelectDialog(Context context){
        super(context,R.style.ActionSheetDialogStyle);
        this.context=context;
        listenerMap=new HashMap<>();
        initView();
    }

    private void initView(){
        root = LayoutInflater.from(context).inflate(R.layout.dialog_list_select, null);
        listView=root.findViewById(R.id.item_list);
        titleView=root.findViewById(R.id.dialog_title);
        adapter=new ListSelectAdapter(context,new ArrayList<>(),new ArrayList<>());
        listView.setAdapter(adapter);

        WindowManager windowManager = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        root.setMinimumWidth(display.getWidth());
        setContentView(root);

        // 设置窗体位置
        Window dialogWindow = getWindow();
        dialogWindow.setGravity(Gravity.LEFT | Gravity.BOTTOM);
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.x = 0;
        lp.y = 0;
        dialogWindow.setAttributes(lp);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                View.OnClickListener listener;
                if((listener=listenerMap.get(position))!=null){
                    dismiss();
                    listener.onClick(view);
                }
            }
        });

        findViewById(R.id.item_cancel).setOnClickListener((v)->{dismiss();});
    }

    // 添加选项
    public ListSelectDialog addItem(String text, int imgId, View.OnClickListener listener){
        int n=adapter.addItem(text,imgId);
        listenerMap.put(n,listener);
        return this;
    }

    // 设置标题
    public ListSelectDialog setTitle(String title){
        this.titleView.setText(title);
        return this;
    }


    public ListSelectDialog showCancelButton(boolean visible){

        if(visible){
            findViewById(R.id.item_cancel_line).setVisibility(View.VISIBLE);
            findViewById(R.id.item_cancel).setVisibility(View.VISIBLE);
        }else{
            findViewById(R.id.item_cancel_line).setVisibility(View.GONE);
            findViewById(R.id.item_cancel).setVisibility(View.GONE);
        }

        return this;
    }

    @Override
    public void show() {
        super.show();
    }

    @Override
    public void dismiss() {
        super.dismiss();
    }

    public class ListSelectAdapter extends BaseAdapter{
        private Context context;
        private List<String> list;
        private List<Integer> imgList;
        private LayoutInflater layoutInflater;
        private ImageView icon;
        private TextView text;
        public ListSelectAdapter(Context context, List<String> list){
            this(context,list,new ArrayList<>());
        }

        public ListSelectAdapter(Context context, List<String> list,List<Integer> imgList){
            this.context=context;
            this.list=list;
            this.imgList=imgList;
            layoutInflater=LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        public int addItem(String text,int img,int index){
            list.add(index,text);
            imgList.add(index,img);
            return list.size()-1;
        }

        public int addItem(String text,int img){
            list.add(text);
            imgList.add(img);
            return list.size()-1;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            convertView = layoutInflater.inflate(R.layout.dialog_view_list_select, null);
            icon=convertView.findViewById(R.id.item_icon);
            if(imgList.size()>position){
                icon.setImageResource(imgList.get(position));
            }
            text=convertView.findViewById(R.id.item_text);
            text.setText(list.get(position));
            return convertView;
        }
    }
}
