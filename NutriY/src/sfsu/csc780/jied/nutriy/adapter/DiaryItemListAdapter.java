package sfsu.csc780.jied.nutriy.adapter;
 
import java.util.HashMap;
import java.util.List;

import sfsu.csc780.jied.nutriy.DiaryPageFragment;
import sfsu.csc780.jied.nutriy.R;
import sfsu.csc780.jied.nutriy.model.DiaryItem;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;
 
public class DiaryItemListAdapter extends BaseExpandableListAdapter {
 
    private Context _context;
    private List<String> _listDataHeader; // header titles
    private List<String> _listDataHeaderValue; // header values
    
    // child data in format of header title, [child title, child calorie]
    private HashMap<String, List<DiaryItem>> _listDataChild;
    
    private final static int CHILD_VIEW_ID = R.layout.diary_item_list_item;
    private final static int CHILD_EDIT_VIEW_ID = R.layout.diary_item_edit_list_item;
    private int child_view_id = CHILD_VIEW_ID;
    
    private int mode;
 
    public DiaryItemListAdapter(Context context, List<String> listDataHeader, 
    		List<String> listDataHeaderValue,
            HashMap<String, List<DiaryItem>> listChildData,
            int mode) {
        this._context = context;
        this._listDataHeader = listDataHeader;
        this._listDataHeaderValue = listDataHeaderValue;
        this._listDataChild = listChildData;
        this.mode = mode;
        child_view_id = (mode == DiaryPageFragment.VIEW_MODE) ? CHILD_VIEW_ID : CHILD_EDIT_VIEW_ID;
    }
 
    @Override
    public Object getChild(int groupPosition, int childPosititon) {
    	String headerTitle = this._listDataHeader.get(groupPosition);
        return this._listDataChild.get(headerTitle).get(childPosititon);
    }
 
    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }
 
    @Override
    public View getChildView(int groupPosition, final int childPosition,
            boolean isLastChild, View convertView, ViewGroup parent) {
 
        final DiaryItem item = (DiaryItem) getChild(groupPosition, childPosition);
 
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(child_view_id, null);
        }
 
        TextView tvChildTitle = (TextView) convertView
                .findViewById(R.id.diary_item_title);
        TextView tvChildCalorie = (TextView) convertView
                .findViewById(R.id.diary_item_calorie);
 
        tvChildTitle.setText(item.getName());
        tvChildCalorie.setText(String.valueOf(item.getCalorie()));
        return convertView;
    }
 
    @Override
    public int getChildrenCount(int groupPosition) {
    	String headerTitle = this._listDataHeader.get(groupPosition);
        return this._listDataChild.get(headerTitle).size();
    }
 
    @Override
    public Object getGroup(int groupPosition) {
    	String group = this._listDataHeader.get(groupPosition);
    	String groupValue = this._listDataHeaderValue.get(groupPosition);
        return new String[] { group, groupValue };
    }
 
    @Override
    public int getGroupCount() {
        return this._listDataHeader.size();
    }
 
    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }
 
    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
            View convertView, ViewGroup parent) {
        String[] header = (String[]) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.diary_item_list_group, null);
        }
 
        TextView tvHeaderTitle = (TextView) convertView
                .findViewById(R.id.diaryItemListHeader);
        TextView tvHeaderValue = (TextView) convertView
                .findViewById(R.id.diaryItemListHeaderValue);
        tvHeaderTitle.setTypeface(null, Typeface.BOLD);
        tvHeaderTitle.setText(header[0]);
        tvHeaderValue.setText(header[1]);
 
        return convertView;
    }
 
    @Override
    public boolean hasStableIds() {
        return false;
    }
 
    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}