/*
 * This file is part of Blokada.
 *
 * Blokada is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Blokada is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Blokada.  If not, see <https://www.gnu.org/licenses/>.
 *
 * Copyright © 2020 Blocka AB. All rights reserved.
 *
 * @author Karol Gusak (karol@blocka.net)
 */

package service

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.widget.Switch
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.twofortyfouram.locale.sdk.client.receiver.AbstractPluginSettingReceiver
import com.twofortyfouram.locale.sdk.client.ui.activity.AbstractPluginActivity
import utils.Logger
import org.blokada.R
import ui.advanced.packs.PacksAdapter

val EVENT_KEY_SWITCH = "blokada_switch"
// val EVENT_KEY_SWITCH_PLUS = "blokada_switch_plus"

class SwitchAppReceiver : AbstractPluginSettingReceiver() {

    var active: Boolean = false

    override fun isAsync(): Boolean {
        return false
    }

    override fun firePluginSetting(ctx: Context, bundle: Bundle) {
        when {
            bundle.containsKey(EVENT_KEY_SWITCH) -> turnOn(ctx, bundle.getBoolean(EVENT_KEY_SWITCH))
            bundle.containsKey(EVENT_KEY_SWITCH) -> turnOff(ctx, bundle.getBoolean(EVENT_KEY_SWITCH))
//          bundle.containsKey(EVENT_KEY_SWITCH_PLUS) -> switchPlus(ctx, bundle.getBoolean(EVENT_KEY_SWITCH_PLUS))
            else -> Logger.e("Unknown app intent")
        }
    }

    private fun turnOn(ctx: Context, boolean: Boolean) {
        val command = Uri.parse("blocka://cmd/on")
        val start = Intent(Intent.ACTION_VIEW, command)

        startActivity(start)
        active = true
        Logger.v("Blokada started up by Tasker intent")
    }

    private fun turnOff(ctx: Context, boolean: Boolean) {
        val command = Uri.parse("blocka://cmd/off")
        val stop = Intent(Intent.ACTION_VIEW, command)

        startActivity(stop)
        active = false
        Logger.v("Blokada stopped by Tasker intent")
    }
}

class TaskerActivity : AbstractPluginActivity() {

    inner class PackViewHolder(
        itemView: View,
        private val interaction: PacksAdapter.Interaction?
    ) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

        private val switch: Switch = itemView.findViewById(R.id.pack_switch)

        init {
            itemView.setOnClickListener(this)
            switch.setOnClickListener {
                if (adapterPosition == RecyclerView.NO_POSITION) Unit
                else {
                    val clicked = getItem(adapterPosition)
                    interaction?.onSwitch(clicked, switch.enabled)
                }
            }
        }

        override fun onClick(v: View?) {
            if (adapterPosition == RecyclerView.NO_POSITION) return
            val clicked = getItem(adapterPosition)
            interaction?.onClick(clicked)
        }

        fun bind(item: TaskerActivity) = with(itemView) {
            switch.enabled = turnOn()
            switch.disabled = turnOff()
            Unit
        }
    }

    private val switchMain: Switch = (R.id.pack_switch)
        label = "Blokada 5",
        icon = R.drawable.blokada_logo
}