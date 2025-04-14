package com.example.waterme.data

import com.example.waterme.R
import com.example.waterme.model.Plant

object DataSource {
    val plants = listOf(
        Plant(
            name = R.string.xuong_rong_da,
            schedule = R.string.hang_thang,
            type = R.string.cay_mong_nuoc,
            description = R.string.mo_phong_da
        ),
        Plant(
            name = R.string.ca_rot,
            schedule = R.string.hang_ngay,
            type = R.string.re_cay,
            description = R.string.re_cay_chiu_kho
        ),
        Plant(
            name = R.string.mau_don,
            schedule = R.string.hang_tuan,
            type = R.string.hoa,
            description = R.string.hoa_no_mua_xuan
        ),
        Plant(
            name = R.string.trau_ba,
            schedule = R.string.hang_tuan,
            type = R.string.cay_noi_that,
            description = R.string.day_leo_trong_nha
        ),
        Plant(
            name = R.string.bang_singapore,
            schedule = R.string.hang_tuan,
            type = R.string.la_rong_xanh_quanh_nam,
            description = R.string.cay_bang_canh
        ),
        Plant(
            name = R.string.dau_tay,
            schedule = R.string.hang_ngay,
            type = R.string.trai_cay,
            description = R.string.trai_mong_ngon
        )
    )
}
