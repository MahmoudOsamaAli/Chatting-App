package com.example.chatbox.main.fragments.home

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.chatbox.R
import com.example.chatbox.data.FakeData
import com.example.chatbox.databinding.FragmentsHomeBinding
import com.example.chatbox.main.fragments.home.recyclerView.HomeChatsAdapter
import com.google.android.material.snackbar.Snackbar

/**
 * A Fragment representing the messages screen.
 * Handles displaying messages in a RecyclerView and allows swipe actions
 * to delete or toggle the silent state of a message.
 */
class HomeFragment : Fragment() {

    private lateinit var binding: FragmentsHomeBinding
    private lateinit var adapter: HomeChatsAdapter
    private lateinit var chatsList: MutableList<FakeData.ChatInfo>
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        // Inflate the layout and return the root view
        binding = FragmentsHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    /**
     * Set up RecyclerView and swipe actions after the view is created.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupSwipeToDeleteAndSilent(binding.recyclerViewMessages)
    }

    /**
     * Initialize the RecyclerView by setting its layout manager,
     * fetching the messages from FakeData, and assigning the adapter.
     */
    private fun setupRecyclerView() {
        val fakeData = FakeData()
        chatsList = fakeData.getChatInfo().toMutableList()
        adapter = HomeChatsAdapter(chatsList, this)
        binding.recyclerViewMessages.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = this@HomeFragment.adapter
        }
    }

    /**
     * Set up swipe gestures for the RecyclerView.
     * Swiping left deletes a message, swiping right toggles the silent state.
     */
    private fun setupSwipeToDeleteAndSilent(recyclerView: RecyclerView) {
        val itemTouchHelper = ItemTouchHelper(object :
            ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean = false // No move actions, only swipe

            /**
             * Handle swipe actions to delete or toggle silent status.
             */
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                handleSwipe(viewHolder.adapterPosition, direction) // Process swipe action
            }

            /**
             * Draw appropriate icons and background during swipe gestures.
             */
            override fun onChildDraw(
                c: Canvas,
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                dX: Float,
                dY: Float,
                actionState: Int,
                isCurrentlyActive: Boolean
            ) {
                drawSwipeIndicators(
                    c, viewHolder, dX
                ) // Draw icons and background based on swipe direction
                super.onChildDraw(
                    c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive
                )
            }
        })

        // Attach ItemTouchHelper to RecyclerView
        itemTouchHelper.attachToRecyclerView(recyclerView)
    }

    /**
     * Handles swipe actions based on the direction.
     * Swiping left deletes the message, swiping right toggles its silent state.
     */
    private fun handleSwipe(position: Int, direction: Int) {
        // Validate the position of the swiped item
        if (position == RecyclerView.NO_POSITION || position < 0 || position >= chatsList.size) return

        when (direction) {
            ItemTouchHelper.LEFT -> {
                // Handle left swipe (delete)
                val deletedItem = adapter.removeItem(position) // Remove message
                if (deletedItem != null) {
                    showUndoSnackbar(deletedItem, position) // Show Snackbar with undo option
                }
            }

            ItemTouchHelper.RIGHT -> {
                // Handle right swipe (toggle silent state)
                adapter.toggleSilentItem(position) // Toggle silent status
                saveSilentState(
                    position, adapter.getSilentState(position)
                ) // Save the new silent state
            }
        }
    }

    /**
     * Displays an undo option in a Snackbar after a message is deleted.
     */
    private fun showUndoSnackbar(deletedItem: FakeData.ChatInfo, position: Int) {
        Snackbar.make(binding.recyclerViewMessages, "Item Deleted", Snackbar.LENGTH_LONG)
            .setAction("Undo") {
                adapter.restoreItem(deletedItem, position) // Restore deleted message on undo
            }.show() // Display the Snack bar
    }

    /**
     * Draws icons and background for swipe actions.
     * Shows silent/un-silent icon for right swipe, delete icon for left swipe.
     */
    private fun drawSwipeIndicators(c: Canvas, viewHolder: RecyclerView.ViewHolder, dX: Float) {
        val itemView = viewHolder.itemView
        val icon: Drawable?
        val background: Drawable?
        val iconMargin = (itemView.height - (ContextCompat.getDrawable(
            requireContext(), R.drawable.ic_recycler_delete
        )?.intrinsicHeight ?: 0)) / 2
        val position = viewHolder.adapterPosition

        // Validate position before drawing
        if (position == RecyclerView.NO_POSITION || position < 0 || position >= adapter.itemCount) {
            return
        }

        // Determine if the message is silent
        val isSilent = adapter.getSilentState(position)

        // Draw based on swipe direction
        if (dX > 0) {
            // Swipe right: show silent/un-silent icon
            icon = if (isSilent) {
                ContextCompat.getDrawable(
                    requireContext(), R.drawable.ic_recycler_notification_silence
                ) // Silent icon
            } else {
                ContextCompat.getDrawable(
                    requireContext(), R.drawable.ic_recycler_notification_active
                ) // Un-silent icon
            }
            background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_swipe_icon_white)
            setBoundsForRightSwipe(itemView, icon, background, dX, iconMargin)
        } else {
            // Swipe left: show delete icon
            icon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_recycler_delete)
            background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_swipe_icon_red)
            setBoundsForLeftSwipe(itemView, icon, background, dX, iconMargin)
        }

        // Draw background and icon
        background?.draw(c)
        icon?.draw(c)
    }

    /**
     * Set the bounds for the silent/un-silent icon and background during right swipe.
     */
    private fun setBoundsForRightSwipe(
        itemView: View, icon: Drawable?, background: Drawable?, dX: Float, iconMargin: Int
    ) {
        val iconLeft = itemView.left + iconMargin
        val iconRight = iconLeft + (icon?.intrinsicWidth ?: 0)
        icon?.setBounds(
            iconLeft, itemView.top + iconMargin, iconRight, itemView.bottom - iconMargin
        )
        background?.setBounds(
            itemView.left, itemView.top, itemView.left + dX.toInt(), itemView.bottom
        )
    }

    /**
     * Set the bounds for the delete icon and background during left swipe.
     */
    private fun setBoundsForLeftSwipe(
        itemView: View, icon: Drawable?, background: Drawable?, dX: Float, iconMargin: Int
    ) {
        val iconLeft = itemView.right - iconMargin - (icon?.intrinsicWidth ?: 0)
        val iconRight = itemView.right - iconMargin
        icon?.setBounds(
            iconLeft, itemView.top + iconMargin, iconRight, itemView.bottom - iconMargin
        )
        background?.setBounds(
            itemView.right + dX.toInt(), itemView.top, itemView.right, itemView.bottom
        )
    }

    /**
     * Saves the silent state of a message in SharedPreferences.
     */
    private fun saveSilentState(position: Int, isSilent: Boolean) {
        context?.getSharedPreferences("SilentStatePrefs", Context.MODE_PRIVATE)?.edit()?.apply {
            putBoolean("message_$position", isSilent) // Store the silent state
            apply() // Commit changes
        }

    }

    fun onItemClick(chatInfo: FakeData.ChatInfo) {
        val userName = chatInfo.name

        val chatFragment = ChatFragment()
        val bundle = Bundle().apply {
            putString("userName", userName)
        }
        chatFragment.arguments = bundle

        val fragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragments_container, chatFragment)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }

}
