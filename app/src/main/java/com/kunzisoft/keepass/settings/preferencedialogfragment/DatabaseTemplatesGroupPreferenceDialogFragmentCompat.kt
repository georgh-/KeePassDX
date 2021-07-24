/*
 * Copyright 2021 Jeremy Jamet / Kunzisoft.
 *
 * This file is part of KeePassDX.
 *
 *  KeePassDX is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  KeePassDX is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with KeePassDX.  If not, see <http://www.gnu.org/licenses/>.
 *
 */
package com.kunzisoft.keepass.settings.preferencedialogfragment

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kunzisoft.keepass.R
import com.kunzisoft.keepass.database.element.Group
import com.kunzisoft.keepass.settings.preferencedialogfragment.adapter.ListRadioItemAdapter

class DatabaseTemplatesGroupPreferenceDialogFragmentCompat
    : DatabaseSavePreferenceDialogFragmentCompat(),
        ListRadioItemAdapter.RadioItemSelectedCallback<Group> {

    private var mGroupTemplates: Group? = null

    override fun onBindDialogView(view: View) {
        super.onBindDialogView(view)

        val recyclerView = view.findViewById<RecyclerView>(R.id.pref_dialog_list)
        recyclerView.layoutManager = LinearLayoutManager(context)

        activity?.let { activity ->
            val groupsAdapter = ListRadioItemAdapter<Group>(activity)
            groupsAdapter.setRadioItemSelectedCallback(this)
            recyclerView.adapter = groupsAdapter

            mDatabase?.let { database ->
                mGroupTemplates = database.templatesGroup
                groupsAdapter.setItems(database.getAllGroupsWithoutRoot(), mGroupTemplates)
            }
        }
    }

    override fun onItemSelected(item: Group) {
        mGroupTemplates = item
    }

    override fun onDialogClosed(positiveResult: Boolean) {
        if (positiveResult) {
            mDatabase?.let { database ->
                if (database.allowConfigurableTemplatesGroup) {
                    val oldGroup = database.templatesGroup
                    val newGroup = mGroupTemplates
                    database.setTemplatesGroup(newGroup)
                    mProgressDatabaseTaskProvider?.startDatabaseSaveTemplatesGroup(
                        oldGroup,
                        newGroup,
                        mDatabaseAutoSaveEnable
                    )
                }
            }
        }
    }

    companion object {

        fun newInstance(
                key: String): DatabaseTemplatesGroupPreferenceDialogFragmentCompat {
            val fragment = DatabaseTemplatesGroupPreferenceDialogFragmentCompat()
            val bundle = Bundle(1)
            bundle.putString(ARG_KEY, key)
            fragment.arguments = bundle

            return fragment
        }
    }
}
