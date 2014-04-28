package com.uxmarket.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.uxmarket.R;
import com.uxmarket.model.TableRow;

import java.util.List;

public class ListArrayAdapter extends ArrayAdapter<TableRow> {
    private final Context context;
    private final List<TableRow> values;

    public ListArrayAdapter(Context context, List<TableRow> values) {
        super(context, R.layout.list_row, values);
        this.context = context;
        this.values = values;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.list_row, parent, false);

        TextView tvSymbol = (TextView) rowView.findViewById(R.id.symbol);
        TextView tvDescription = (TextView) rowView.findViewById(R.id.description);
        TextView tvPrice = (TextView) rowView.findViewById(R.id.price);
        TextView tvPercentCnange = (TextView) rowView.findViewById(R.id.percent_change);

        TableRow row = values.get(position);

        tvSymbol.setText(row.symbol);
        tvDescription.setText(row.description);
        tvPrice.setText(row.price);
        tvPercentCnange.setText(row.percentChange);

        if (row.percentChange.startsWith("-")) {
            tvPercentCnange.setBackgroundResource(R.drawable.red_round_rectangle);
        } else {
            tvPercentCnange.setBackgroundResource(R.drawable.green_round_rectangle);
        }

        return rowView;
    }
}
