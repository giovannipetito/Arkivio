package it.giovanni.arkivio.fragments.detail.nearby.chat

import android.content.Context
import android.content.DialogInterface
import android.widget.ArrayAdapter
import androidx.appcompat.app.AlertDialog
import java.util.*

/**
 * Simple wrapper for an AlertDialog with a list of items, each of which has a display string and
 * and associated value (similar to a "select" tag in HTML).
 */
class NearbyDialog(context: Context, builder: AlertDialog.Builder, listener: DialogInterface.OnClickListener) {

    private val mDialog: AlertDialog
    private val mAdapter: ArrayAdapter<String> = ArrayAdapter(context, android.R.layout.select_dialog_singlechoice)
    private val mItemMap: HashMap<String, String> = HashMap()

    init {
        // Create dialog from builder
        builder.setAdapter(mAdapter, listener)
        mDialog = builder.create()
    }

    /**
     * Add an item to the Dialog's list.
     *
     * @param title the human-readable string that should be used to display the item.
     * @param value a value associated with the item that should not be displayed.
     */
    fun addItem(title: String, value: String) {
        mItemMap[title] = value
        mAdapter.add(title)
    }

    // Remove an item from the list by its title.
    fun removeItemByTitle(title: String) {
        mItemMap.remove(title)
        mAdapter.remove(title)
    }

    /**
     * Remove an item from the list by its associated value.
     * Note: this is an O(n) operation.
     *
     * @param value the value of the item to remove.
     */
    fun removeItemByValue(value: String) {
        val iterator: MutableIterator<Map.Entry<String, String>> = mItemMap.entries.iterator()
        while (iterator.hasNext()) {
            val entry = iterator.next()
            if (entry.value == value) {
                iterator.remove()
                mAdapter.remove(entry.key)
                mAdapter.notifyDataSetChanged()
            }
        }
    }

    /**
     * Get the title of the item at an index,
     *
     * @param index the index of the item in the list.
     * @return the item's title.
     */
    fun getItemKey(index: Int): String {
        return mAdapter.getItem(index)!!
    }

    /**
     * Get the value of an item at an index.
     *
     * @param index the index of the item in the list.
     * @return the item's value.
     */
    fun getItemValue(index: Int): String? {
        return mItemMap[getItemKey(index)]
    }

    /**
     * Show the dialog (calls AlertDialog#show).
     */
    fun show() {
        mDialog.show()
    }

    /**
     * Dismiss the dialog if it is showing (calls AlertDialog#dismiss).
     */
    fun dismiss() {
        if (mDialog.isShowing) {
            mDialog.dismiss()
        }
    }
}