package com.example.chatbox.main.fragments.home

import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.chatbox.R
import com.example.chatbox.data.ChatInfo
import com.example.chatbox.data.FakeData
import com.example.chatbox.databinding.FragmentHomeBinding
import com.example.chatbox.main.fragments.home.Chats.ChatMessages.ChatActivity
import com.example.chatbox.main.fragments.home.Chats.HomeChatsAdapter
import com.example.chatbox.main.fragments.home.Stories.StatusAdapter
import com.example.chatbox.repository.FireBaseAuthRepo
import com.google.android.material.snackbar.Snackbar

class HomeFragment : Fragment() {

    companion object {
        const val Tag = "HomeFragment"
    }

    private lateinit var homeViewModel: HomeViewModel
    private lateinit var binding: FragmentHomeBinding
    private lateinit var chatsAdapter: HomeChatsAdapter
    private lateinit var statusAdapter: StatusAdapter
    private lateinit var statusList: List<FakeData.StatusInfo>
    private var chatsList: MutableList<ChatInfo> = mutableListOf()
    private val currentUserId = FireBaseAuthRepo.getUserUUID()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        homeViewModel = ViewModelProvider(this)[HomeViewModel::class.java]
        loadProfilePicture()
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupChatsRV()
        setupSwipeToDeleteAndSilent(binding.homeRvChats)
        setupStatusRV()
    }

    private fun loadProfilePicture() {
        homeViewModel.profilePic.observe(viewLifecycleOwner) {
            Glide.with(this)
                .load(it)
                .placeholder(R.drawable.img_profile_empty)
                .into(binding.homeImgProfile)

        }
        currentUserId?.let { homeViewModel.getProfilePicLink(it) }
    }

    private fun setupStatusRV() {
        statusList = FakeData().getStatusInfo().toMutableList()
        statusAdapter = StatusAdapter(statusList)
        binding.homeRvStatus.adapter = this@HomeFragment.statusAdapter
    }

    private fun setupChatsRV() {
        val manager = LinearLayoutManager(requireContext()).apply {
            reverseLayout = true
            stackFromEnd = true
            isSmoothScrollbarEnabled = true
        }
        binding.homeRvChats.layoutManager = manager
        chatsAdapter = HomeChatsAdapter(emptyList(), currentUserId!!, this)
        binding.homeRvChats.adapter = chatsAdapter
        homeViewModel.chatList.observe(viewLifecycleOwner) { chatList ->
            chatsAdapter.updateChats(chatList)
            binding.homeRvChats.post {
                binding.homeRvChats.smoothScrollToPosition(0)

            }
        }
    }

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
        if (position in 0 until chatsAdapter.itemCount) {
            when (direction) {
                ItemTouchHelper.LEFT -> {
                    chatsAdapter.removeItem(position)?.let { deletedItem ->
                        showUndoSnackBar(deletedItem, position)
                    }
                }

                ItemTouchHelper.RIGHT -> {
                    chatsAdapter.toggleSilentItem(position)
                    saveSilentState(position, chatsAdapter.getSilentState(position))
                }
            }
            chatsAdapter.notifyItemChanged(position)
        } else {
            chatsAdapter.notifyDataSetChanged()
        }
    }

    /**
     * Displays an undo option in a Snackbar after a message is deleted.
     */
    private fun showUndoSnackBar(deletedItem: ChatInfo, position: Int) {
        Snackbar.make(binding.homeRvChats, "Item Deleted", Snackbar.LENGTH_LONG)
            .setAction("Undo") {
                chatsAdapter.restoreItem(deletedItem, position) // Restore deleted message on undo
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
        if (position == RecyclerView.NO_POSITION || position < 0 || position >= chatsAdapter.itemCount) {
            return
        }

        // Determine if the message is silent
        val isSilent = chatsAdapter.getSilentState(position)

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

    private fun saveSilentState(position: Int, isSilent: Boolean) {
        context?.getSharedPreferences("SilentStatePrefs", Context.MODE_PRIVATE)?.edit()?.apply {
            putBoolean("message_$position", isSilent) // Store the silent state
            apply() // Commit changes
        }
    }
    fun openChatActivity(name: String, phoneNumber: String, profilePic: String) {
        val intent = Intent(requireContext(), ChatActivity::class.java)
        intent.putExtra("HomePhoneNumber", phoneNumber)
        requireContext().startActivity(intent)
    }

}
