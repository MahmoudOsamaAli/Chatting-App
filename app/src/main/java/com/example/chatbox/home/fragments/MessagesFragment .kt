package com.example.chatbox.home.fragments

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
import com.example.chatbox.databinding.FragmentsMessageBinding
import com.example.chatbox.home.recyclerView.FakeData
import com.example.chatbox.home.recyclerView.MessagesAdapter
import com.google.android.material.snackbar.Snackbar

/**
 * A Fragment representing the messages screen.
 * Handles displaying messages in a RecyclerView and allows swipe actions
 * to delete or toggle the silent state of a message.
 */
class MessageFragment : Fragment() {

    // View binding to access views in the fragment
    private lateinit var binding: FragmentsMessageBinding

    // Adapter for managing and displaying messages in RecyclerView
    private lateinit var adapter: MessagesAdapter

    // List of messages from FakeData class
    private lateinit var messagesList: MutableList<FakeData.Message>

    /**
     * Inflate the fragment's layout using ViewBinding.
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout and return the root view
        binding = FragmentsMessageBinding.inflate(inflater, container, false)
        return binding.root
    }

    /**
     * Set up RecyclerView and swipe actions after the view is created.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView() // Initialize RecyclerView with messages
        setupSwipeToDeleteAndSilent(binding.recyclerViewMessages) // Set up swipe gestures
    }

    /**
     * Initialize the RecyclerView by setting its layout manager,
     * fetching the messages from FakeData, and assigning the adapter.
     */
    private fun setupRecyclerView() {
        // Get a list of fake messages
        val fakeData = FakeData()
        messagesList = fakeData.getMessages().toMutableList() // Get messages from FakeData

        // Create a list for the silent status of each message
        val silentStatusList = messagesList.map { it.isSilent }.toMutableList()

        // Initialize adapter with messages and their silent statuses
        adapter = MessagesAdapter(messagesList, silentStatusList)

        // Set RecyclerView layout manager and adapter
        binding.recyclerViewMessages.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = this@MessageFragment.adapter
        }
    }

    /**
     * Set up swipe gestures for the RecyclerView.
     * Swiping left deletes a message, swiping right toggles the silent state.
     */
    private fun setupSwipeToDeleteAndSilent(recyclerView: RecyclerView) {
        val itemTouchHelper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
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
                c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder,
                dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean
            ) {
                drawSwipeIndicators(c, viewHolder, dX) // Draw icons and background based on swipe direction
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
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
        if (position == RecyclerView.NO_POSITION || position < 0 || position >= messagesList.size) return

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
                saveSilentState(position, adapter.getSilentState(position)) // Save the new silent state
            }
        }
    }

    /**
     * Displays an undo option in a Snackbar after a message is deleted.
     */
    private fun showUndoSnackbar(deletedItem: FakeData.Message, position: Int) {
        Snackbar.make(binding.recyclerViewMessages, "Item Deleted", Snackbar.LENGTH_LONG)
            .setAction("Undo") {
                adapter.restoreItem(deletedItem, position) // Restore deleted message on undo
            }
            .show() // Display the Snackbar
    }

    /**
     * Draws icons and background for swipe actions.
     * Shows silent/un-silent icon for right swipe, delete icon for left swipe.
     */
    private fun drawSwipeIndicators(c: Canvas, viewHolder: RecyclerView.ViewHolder, dX: Float) {
        val itemView = viewHolder.itemView
        val icon: Drawable?
        val background: Drawable?
        val iconMargin = (itemView.height - (ContextCompat.getDrawable(requireContext(), R.drawable.delete_message)?.intrinsicHeight ?: 0)) / 2
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
                ContextCompat.getDrawable(requireContext(), R.drawable.silent_swipe_message) // Silent icon
            } else {
                ContextCompat.getDrawable(requireContext(), R.drawable.un_silent_swipe_message) // Un-silent icon
            }
            background = ContextCompat.getDrawable(requireContext(), R.drawable.swipe_silent)
            setBoundsForRightSwipe(itemView, icon, background, dX, iconMargin)
        } else {
            // Swipe left: show delete icon
            icon = ContextCompat.getDrawable(requireContext(), R.drawable.delete_message)
            background = ContextCompat.getDrawable(requireContext(), R.drawable.swipe_delete)
            setBoundsForLeftSwipe(itemView, icon, background, dX, iconMargin)
        }

        // Draw background and icon
        background?.draw(c)
        icon?.draw(c)
    }

    /**
     * Set the bounds for the silent/un-silent icon and background during right swipe.
     */
    private fun setBoundsForRightSwipe(itemView: View, icon: Drawable?, background: Drawable?, dX: Float, iconMargin: Int) {
        val iconLeft = itemView.left + iconMargin
        val iconRight = iconLeft + (icon?.intrinsicWidth ?: 0)
        icon?.setBounds(iconLeft, itemView.top + iconMargin, iconRight, itemView.bottom - iconMargin)
        background?.setBounds(itemView.left, itemView.top, itemView.left + dX.toInt(), itemView.bottom)
    }

    /**
     * Set the bounds for the delete icon and background during left swipe.
     */
    private fun setBoundsForLeftSwipe(itemView: View, icon: Drawable?, background: Drawable?, dX: Float, iconMargin: Int) {
        val iconLeft = itemView.right - iconMargin - (icon?.intrinsicWidth ?: 0)
        val iconRight = itemView.right - iconMargin
        icon?.setBounds(iconLeft, itemView.top + iconMargin, iconRight, itemView.bottom - iconMargin)
        background?.setBounds(itemView.right + dX.toInt(), itemView.top, itemView.right, itemView.bottom)
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
}
