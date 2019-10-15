package io.numbers.mediant.ui

import android.view.MenuItem

interface OnItemMenuClickListener {

    fun onItemMenuClick(position: Int, menuItem: MenuItem): Boolean
}