package nl.mwsoft.www.chatster.modelLayer.helper.recyclerView;

import android.view.View;


public interface RecyclerViewClickListener {
    void onClick(View view, int position);

    void onLongClick(View view, int position);
}
