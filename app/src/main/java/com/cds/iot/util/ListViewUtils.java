package com.cds.iot.util;

import android.view.View;
import android.widget.ListView;

public class ListViewUtils {
  /**
   * 通过位置找到ListView中的某个item的View
   * @param pos
   * @param listView
   * @return
   */
  public static View getViewByPosition(int pos, ListView listView) {
    int firstListItemPosition = listView.getFirstVisiblePosition();
    int lastListItemPosition = firstListItemPosition
            + listView.getChildCount() - 1;

    if (pos < firstListItemPosition || pos > lastListItemPosition) {
      return listView.getAdapter().getView(pos, null, listView);
    } else {
      final int childIndex = pos - firstListItemPosition;
      return listView.getChildAt(childIndex);
    }
  }
}
