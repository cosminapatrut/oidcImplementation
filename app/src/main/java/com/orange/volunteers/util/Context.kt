package com.orange.volunteers.util

import android.app.Activity
import android.content.Context
import android.content.Intent
import com.orange.essentials.otb.OtbActivity
import com.orange.essentials.otb.manager.TrustBadgeElementFactory
import com.orange.essentials.otb.manager.TrustBadgeManager
import com.orange.essentials.otb.model.Term
import com.orange.essentials.otb.model.TrustBadgeElement
import com.orange.essentials.otb.model.type.GroupType
import com.orange.essentials.otb.model.type.TermType
import com.orange.essentials.otb.model.type.UserPermissionStatus
import com.orange.volunteers.R
import java.lang.annotation.ElementType


fun Context.startTrustBadge(activity: Activity) {
    val trustBadgeElements = ArrayList<TrustBadgeElement>()

    val analyticsElement = TrustBadgeElementFactory.build(
        applicationContext, GroupType.IMPROVEMENT_PROGRAM,
        com.orange.essentials.otb.model.type.ElementType.APP_DATA
    ).apply {
        userPermissionStatus = UserPermissionStatus.GRANTED
        isToggable = false
        descriptionKey = applicationContext.getString(R.string.info_analytics)
    }

    val location = applicationContext.buildPermissionElement(GroupType.LOCATION, applicationContext.getString(R.string.info_location_description))
//    val camera = applicationContext.buildPermissionElement(GroupType.CAMERA, applicationContext.getString(R.string.info_camera_description))
//    val storage = applicationContext.buildPermissionElement(GroupType.STORAGE, applicationContext.getString(R.string.info_storage_description))
//    val phone = applicationContext.buildPermissionElement(GroupType.PHONE, applicationContext.getString(R.string.info_phone_description))
//    val contacts = applicationContext.buildPermissionElement(GroupType.CONTACTS, applicationContext.getString(R.string.info_contacts_description))

    trustBadgeElements.addAll(listOf(location, analyticsElement))

    val terms = ArrayList<Term>()
    terms.add(Term(TermType.TEXT, R.string.info_trust_badge_commitment_title, R.string.info_trust_badge_commitment_content))

    TrustBadgeManager.INSTANCE.initialize(applicationContext, trustBadgeElements, terms)

    val intent = Intent(activity, OtbActivity::class.java)
    startActivity(intent)
}

private fun Context.buildPermissionElement(
    groupType: GroupType,
    description: String
) = TrustBadgeElementFactory.build(this, groupType,
    com.orange.essentials.otb.model.type.ElementType.PERMISSIONS
).apply {
    descriptionKey = description
}