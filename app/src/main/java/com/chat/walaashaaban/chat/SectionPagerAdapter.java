package com.chat.walaashaaban.chat;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by walaa on 11/10/17.
 */

class SectionPagerAdapter extends FragmentPagerAdapter{
    public SectionPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){

            case 0 :

            FriendsFragment friendsFragment = new FriendsFragment();
            return friendsFragment;

            case 1 :
                ChatsFragment chatsFragment = new ChatsFragment();
                return chatsFragment;

            case 2:
                RequestsFragment requestsFragment = new RequestsFragment();
                return requestsFragment;

            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 3;
    }

    public CharSequence getPageTitle(int position){

        switch (position){

            case 0:
                return "Friends";
            case 1:
                return "Chats";
            case 2:
                return "Requests";
                default:
                    return null;
        }
    }
}
