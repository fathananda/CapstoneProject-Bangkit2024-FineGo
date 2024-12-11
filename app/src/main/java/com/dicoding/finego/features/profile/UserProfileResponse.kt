package com.dicoding.finego.features.profile


data class UserProfileResponse(
    val status: String,
    val data: ProfileDataResponse
)

data class ProfileDataResponse(
    val profileData: Profile
)
