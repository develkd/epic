package de.master.kd.epic.view.position;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import de.master.kd.epic.R;
import de.master.kd.epic.domain.position.Position;
import de.master.kd.epic.services.PictureService;
import de.master.kd.epic.utils.Converter;

/**
 * Created by pentax on 25.07.17.
 */

public class PositionItemAdatper extends ArrayAdapter<Position> {

    public PositionItemAdatper(Context context, List<Position> positionList){
        super(context, R.layout.custom_position_list_item,positionList);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if(null == convertView){
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.custom_position_list_item,null,true);
        }
        ImageView pic = (ImageView) convertView.findViewById(R.id.custom_pic);
        ImageView map = (ImageView) convertView.findViewById(R.id.custom_map);
        TextView titel = (TextView) convertView.findViewById(R.id.titel);
        TextView date = (TextView) convertView.findViewById(R.id.date);
        TextView description = (TextView) convertView.findViewById(R.id.description);

        Position item = getItem(position);
        addPictureToView(pic, item.getPicturePath());
        addPictureToView(map,item.getMapPath());
        titel.setText(item.getTitle());
        date.setText(Converter.toStringDate(item.getCreateDate()));
        description.setText(item.getDescription());

        return convertView;
    }


    private void addPictureToView(ImageView view, String picturePath){
        Bitmap bitmap = PictureService.loadImage(getContext(),picturePath);
        if(null != bitmap) {
            view.setImageBitmap(bitmap);
        }
    }
}


