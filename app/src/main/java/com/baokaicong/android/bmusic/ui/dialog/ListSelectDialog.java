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
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.baokaicong.android.bmusic.R;
import com.baokaicong.android.bmusic.ui.adapter.MenuListAdapter;
import com.baokaicong.android.bmusic.ui.view.ListRefreshFooterView;

import java.util.ArrayList;
import java.util.List;

public class ListSelectDialog {
    private ListView listView;
    private Dialog dialog;
    private Context context;
    private View root;
    private TextView titleView;


    private ListSelectDialog(Context context){
        this.context=context;
    }

    public static ListSelectDialogBuilder builder(Context context){

        return new ListSelectDialogBuilder(context);
    }

    public static class ListSelectDialogBuilder{
        private Context context;
        private String title;
        private List<String> list;
        private List<Integer> imgList;
        private AdapterView.OnItemClickListener listener;
        public ListSelectDialogBuilder(Context context){
            this.context=context;
            list=new ArrayList<>();
            imgList=new ArrayList<>();
        }
        public ListSelectDialogBuilder item(String text){
            return item(text,-1);
        }
        public ListSelectDialogBuilder item(String text,int img){
            if(img!=-1){
                imgList.add(img);
            }
            list.add(text);
            return this;
        }

        public ListSelectDialogBuilder listener(AdapterView.OnItemClickListener listener){
            this.listener=listener;
            return this;
        }

        public ListSelectDialogBuilder title(String text){
            this.title=text;
            return this;
        }

        public ListSelectDialog create(){
            ListSelectDialog dialog=new ListSelectDialog(context);
            dialog.initView();
            dialog.setTitle(title);
            dialog.loadListData(list,imgList);
            if(listener!=null){
                dialog.setOnItemClickListener(listener);
            }
            return dialog;
        }
    }

    private void initView(){
        root = LayoutInflater.from(context).inflate(R.layout.dialog_list_select, null);
        listView=root.findViewById(R.id.item_list);
        titleView=root.findViewById(R.id.dialog_title);
        WindowManager windowManager = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        root.setMinimumWidth(display.getWidth());
        dialog = new Dialog(context, R.style.ActionSheetDialogStyle);
        dialog.setContentView(root);
        Window dialogWindow = dialog.getWindow();
        dialogWindow.setGravity(Gravity.LEFT | Gravity.BOTTOM);
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.x = 0;
        lp.y = 0;
        dialogWindow.setAttributes(lp);
    }

    public ListSelectDialog setOnItemClickListener(AdapterView.OnItemClickListener listener){
        if(listener!=null){
            listView.setOnItemClickListener(listener);
        }
        return this;
    }

    public ListSelectDialog loadListData(List<String> list,List<Integer> imgList){
        ListSelectAdapter adapter=new ListSelectAdapter(context,list,imgList);
        listView.setAdapter(adapter);
        return this;
    }

    public ListSelectDialog setTitle(String title){
        this.titleView.setText(title);
        return this;
    }

    public void show(){
        if (dialog != null) {
            dialog.show();
        }
    }

    public void dismiss(){
        try {
            if (dialog != null && dialog.isShowing()) {
                dialog.dismiss();
            }
            dialog = null;
        } catch (Exception e) {

        }
    }

    public class ListSelectAdapter extends BaseAdapter{
        private Context context;
        private List<String> list;
        private List<Integer> imgList;
        private LayoutInflater layoutInflater;
        private ImageView icon;
        private TextView text;
        public ListSelectAdapter(Context context, List<String> list){
            this(context,list,null);
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

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            convertView = layoutInflater.inflate(R.layout.dialog_view_list_select, null);
            icon=convertView.findViewById(R.id.item_icon);
            if(imgList!=null && imgList.size()>position){
                icon.setImageResource(imgList.get(position));
            }
            text=convertView.findViewById(R.id.item_text);
            if(list!=null){
                text.setText(list.get(position));
            }
            return convertView;
        }
    }
}
