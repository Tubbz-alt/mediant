package io.numbers.mediant.ui.listeners

import android.view.MenuItem

interface ItemMenuClickListener {

    fun onItemMenuClick(position: Int, menuItem: MenuItem): Boolean
}