package com.example.telegraf.ui.objects

import android.graphics.drawable.Drawable
import android.net.Uri
import android.view.View
import android.widget.ImageView
import androidx.drawerlayout.widget.DrawerLayout
import com.example.telegraf.R
import com.example.telegraf.ui.fragments.ContactsFragment
import com.example.telegraf.ui.fragments.SettingsFragment
import com.example.telegraf.utilities.APP_ACTIVITY
import com.example.telegraf.database.USER
import com.example.telegraf.utilities.downloadAndSetImage
import com.example.telegraf.utilities.replaceFragment
import com.mikepenz.materialdrawer.AccountHeader
import com.mikepenz.materialdrawer.AccountHeaderBuilder
import com.mikepenz.materialdrawer.Drawer
import com.mikepenz.materialdrawer.DrawerBuilder
import com.mikepenz.materialdrawer.model.DividerDrawerItem
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem
import com.mikepenz.materialdrawer.model.ProfileDrawerItem
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem
import com.mikepenz.materialdrawer.util.AbstractDrawerImageLoader
import com.mikepenz.materialdrawer.util.DrawerImageLoader

class AppDrawer {
    private lateinit var drawer: Drawer;
    private lateinit var header: AccountHeader;
    private lateinit var drawerLayout: DrawerLayout;
    private lateinit var currentProfile: ProfileDrawerItem;

    fun create() {
        initLoader();
        createHeader();
        createDrawer();
        drawerLayout = drawer.drawerLayout;
    }

    fun drawerEnable() {
        APP_ACTIVITY.supportActionBar?.setDisplayHomeAsUpEnabled(false);
        drawer.actionBarDrawerToggle?.isDrawerIndicatorEnabled = true;
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
        APP_ACTIVITY.toolbar.setNavigationOnClickListener {
            drawer.openDrawer();
        }
    }

    fun drawerDisable() {
        drawer.actionBarDrawerToggle?.isDrawerIndicatorEnabled = false;
        APP_ACTIVITY.supportActionBar?.setDisplayHomeAsUpEnabled(true);
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        APP_ACTIVITY.toolbar.setNavigationOnClickListener {
            APP_ACTIVITY.supportFragmentManager.popBackStack()
        }

    }

    private fun createDrawer() {
        drawer = DrawerBuilder()
            .withActivity(APP_ACTIVITY)
            .withToolbar(APP_ACTIVITY.toolbar)
            .withActionBarDrawerToggle(true)
            .withSelectedItem(-1)
            .withAccountHeader(header)
            .addDrawerItems(
                PrimaryDrawerItem().withIdentifier(100)
                    .withIconTintingEnabled(true)
                    .withName(R.string.create_group)
                    .withSelectable(false)
                    .withIcon(R.drawable.ic_menu_create_groups),
                PrimaryDrawerItem().withIdentifier(101)
                    .withIconTintingEnabled(true)
                    .withName(R.string.secret_chat)
                    .withSelectable(false)
                    .withIcon(R.drawable.ic_menu_secret_chat),
                PrimaryDrawerItem().withIdentifier(102)
                    .withIconTintingEnabled(true)
                    .withName(R.string.create_channel)
                    .withSelectable(false)
                    .withIcon(R.drawable.ic_menu_create_channel),
                PrimaryDrawerItem().withIdentifier(103)
                    .withIconTintingEnabled(true)
                    .withName(R.string.contacts)
                    .withSelectable(false)
                    .withIcon(R.drawable.ic_menu_contacts),
                PrimaryDrawerItem().withIdentifier(104)
                    .withIconTintingEnabled(true)
                    .withName(R.string.phone)
                    .withSelectable(false)
                    .withIcon(R.drawable.ic_menu_phone),
                PrimaryDrawerItem().withIdentifier(105)
                    .withIconTintingEnabled(true)
                    .withName(R.string.favorites)
                    .withSelectable(false)
                    .withIcon(R.drawable.ic_menu_favorites),
                PrimaryDrawerItem().withIdentifier(106)
                    .withIconTintingEnabled(true)
                    .withName(R.string.settings)
                    .withSelectable(false)
                    .withIcon(R.drawable.ic_menu_settings),
                DividerDrawerItem(),
                PrimaryDrawerItem().withIdentifier(107)
                    .withIconTintingEnabled(true)
                    .withName(R.string.invite)
                    .withSelectable(false)
                    .withIcon(R.drawable.ic_menu_invate),
                PrimaryDrawerItem().withIdentifier(108)
                    .withIconTintingEnabled(true)
                    .withName(R.string.help)
                    .withSelectable(false)
                    .withIcon(R.drawable.ic_menu_help)
            ).withOnDrawerItemClickListener(object : Drawer.OnDrawerItemClickListener {
                override fun onItemClick(
                    view: View?,
                    position: Int,
                    drawerItem: IDrawerItem<*>
                ): Boolean {
                    return clickOnItem(position);
                }
            }).build();
    }

    private fun clickOnItem(position: Int): Boolean {
        return when (position) {
            4 -> {
                replaceFragment(ContactsFragment());
                true;
            }
            7 -> {
                replaceFragment(SettingsFragment())
                true;
            }
            else -> false
        }
    }

    private fun createHeader() {
        currentProfile = ProfileDrawerItem()
            .withName(USER.fullname)
            .withEmail(USER.phone)
            .withIcon(USER.photoUrl)
            .withIdentifier(101)
        header = AccountHeaderBuilder()
            .withActivity(APP_ACTIVITY)
            .withHeaderBackground(R.drawable.header)
            .addProfiles(
                currentProfile
            ).build();
    }

    fun updateHeader() {
        currentProfile
            .withName(USER.fullname)
            .withEmail(USER.phone)
            .withIcon(USER.photoUrl)

        header.updateProfile(currentProfile)
    }

    private fun initLoader() {
        DrawerImageLoader.init(object : AbstractDrawerImageLoader() {
            override fun set(imageView: ImageView, uri: Uri, placeholder: Drawable) {
                imageView.downloadAndSetImage(uri.toString())
            }
        })
    }

}