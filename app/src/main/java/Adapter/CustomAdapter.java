package Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.osmdroidex2.R;

import java.util.List;

public class CustomAdapter extends BaseAdapter {
    private final Context context;
    private List<String> items;

    public CustomAdapter(Context context, List<String> items) {
        this.context = context;
        this.items = items;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public String getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View itemView = inflater.inflate(R.layout.list_item, parent, false);

        TextView textView = itemView.findViewById(R.id.floor_number);
        textView.setText(items.get(position));

        return itemView;
    }

    public void updateItems(List<String> updatedItems) {
        items = updatedItems;
        notifyDataSetChanged();
    }
}



