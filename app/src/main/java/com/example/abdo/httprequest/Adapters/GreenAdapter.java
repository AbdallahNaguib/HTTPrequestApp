/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.abdo.httprequest.Adapters;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.abdo.httprequest.Utilities.ColorUtils;
import com.example.abdo.httprequest.R;

import java.util.ArrayList;

/**
 * We couldn't come up with a good name for this class. Then, we realized
 * that this lesson is about RecyclerView.
 *
 * RecyclerView... Recycling... Saving the planet? Being green? Anyone?
 * #crickets
 *
 * Avoid unnecessary garbage collection by using RecyclerView and ViewHolders.
 *
 * If you don't like our puns, we named this Adapter GreenAdapter because its
 * contents are green.
 */
public class GreenAdapter extends
        RecyclerView.Adapter<GreenAdapter.NumberViewHolder>
        implements Parcelable{

    private static final String TAG = GreenAdapter.class.getSimpleName();

    private int mNumberItems;

    public ArrayList<String> getNames() {
        return names;
    }

    private ArrayList<String> names;
    private int count;
    private ListItemClickedListener itemClickedListener;

    public GreenAdapter(ArrayList<String> names,ListItemClickedListener itemClickedListener) {
        mNumberItems = names.size();
        this.itemClickedListener=itemClickedListener;
        this.names=names;
        count=0;
    }

    private GreenAdapter(Parcel in) {
        mNumberItems = in.readInt();
        names = in.createStringArrayList();
        count = in.readInt();
    }

    public static final Creator<GreenAdapter> CREATOR = new Creator<GreenAdapter>() {
        @Override
        public GreenAdapter createFromParcel(Parcel in) {
            return new GreenAdapter(in);
        }

        @Override
        public GreenAdapter[] newArray(int size) {
            return new GreenAdapter[size];
        }
    };

    @NonNull
    /**
    *
    * This gets called when each new ViewHolder is created. This happens when the RecyclerView
    * is laid out. Enough ViewHolders will be created to fill the screen and allow for scrolling.
    *
    * @param viewGroup The ViewGroup that these ViewHolders are contained within.
    * @param viewType  If your RecyclerView has more than one type of item (which ours doesn't) you
    *                  can use this viewType integer to provide a different layout. See
    *                  for more details.
    * @return A new NumberViewHolder that holds the View for each list item
    */
    @Override
    public NumberViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context=parent.getContext();
        int layoutIdForListItem= R.layout.list_item;
        LayoutInflater inflater=LayoutInflater.from(context);
        View view=inflater.inflate(layoutIdForListItem,parent,false);
        NumberViewHolder viewHolder=new NumberViewHolder(view);
        view.setBackgroundColor(
                ColorUtils.
                        getViewHolderBackgroundColorFromInstance(context,count));
        return viewHolder;
    }
    /**
     * OnBindViewHolder is called by the RecyclerView to display the data at the specified
     * position. In this method, we update the contents of the ViewHolder to display the correct
     * indices in the list for this particular position, using the "position" argument that is conveniently
     * passed into us.
     *
     * @param holder   The ViewHolder which should be updated to represent the contents of the
     *                 item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(@NonNull NumberViewHolder holder, int position) {
        holder.bind(names.get(position));
        count++;
    }

    /**
     * This method simply returns the number of items to display. It is used behind the scenes
     * to help layout our Views and for animations.
     *
     * @return The number of items available in our forecast
     */
    @Override
    public int getItemCount() {

        return mNumberItems;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(mNumberItems);
        parcel.writeStringList(names);
        parcel.writeInt(count);
    }


    public interface ListItemClickedListener{

        void onListItemClicked(int index);
    }
    /*
     * Cache of the children views for a list item.
     */
    class NumberViewHolder extends RecyclerView.ViewHolder
        implements View.OnClickListener{

        TextView nameTV;
        NumberViewHolder(View itemView) {
            super(itemView);
            nameTV=itemView.findViewById(R.id.tv_item_number);
            itemView.setOnClickListener(this);
        }
        void bind(String name){
            nameTV.setText(name+" "+count);
        }

        @Override
        public void onClick(View view) {
            Log.d(TAG,"onclick");
            itemClickedListener.onListItemClicked(getAdapterPosition());
        }
    }
}
