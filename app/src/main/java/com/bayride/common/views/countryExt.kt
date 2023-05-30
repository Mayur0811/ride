package com.bayride.common.views

import com.bayride.R
import java.util.ArrayList

fun getFlag(flag: String): Int {
    return when (flag) {
        "ad" -> R.drawable.flag_andorra
        "ae" -> R.drawable.flag_uae
        "af" -> R.drawable.flag_afghanistan
        "ag" -> R.drawable.flag_antigua_and_barbuda
        "ai" -> R.drawable.flag_anguilla
        "al" -> R.drawable.flag_albania
        "am" -> R.drawable.flag_armenia
        "ao" -> R.drawable.flag_angola
        "aq" -> R.drawable.flag_antarctica
        "ar" -> R.drawable.flag_argentina
        "as" -> R.drawable.flag_american_samoa
        "at" -> R.drawable.flag_austria
        "au" -> R.drawable.flag_australia
        "aw" -> R.drawable.flag_aruba
        "ax" -> R.drawable.flag_aland
        "az" -> R.drawable.flag_azerbaijan
        "ba" -> R.drawable.flag_bosnia
        "bb" -> R.drawable.flag_barbados
        "bd" -> R.drawable.flag_bangladesh
        "be" -> R.drawable.flag_belgium
        "bf" -> R.drawable.flag_burkina_faso
        "bg" -> R.drawable.flag_bulgaria
        "bh" -> R.drawable.flag_bahrain
        "bi" -> R.drawable.flag_burundi
        "bj" -> R.drawable.flag_benin
        "bl" -> R.drawable.flag_saint_barthelemy // custom
        "bm" -> R.drawable.flag_bermuda
        "bn" -> R.drawable.flag_brunei
        "bo" -> R.drawable.flag_bolivia
        "br" -> R.drawable.flag_brazil
        "bs" -> R.drawable.flag_bahamas
        "bt" -> R.drawable.flag_bhutan
        "bw" -> R.drawable.flag_botswana
        "by" -> R.drawable.flag_belarus
        "bz" -> R.drawable.flag_belize
        "ca" -> R.drawable.flag_canada
        "cc" -> R.drawable.flag_cocos // custom
        "cd" -> R.drawable.flag_democratic_republic_of_the_congo
        "cf" -> R.drawable.flag_central_african_republic
        "cg" -> R.drawable.flag_republic_of_the_congo
        "ch" -> R.drawable.flag_switzerland
        "ci" -> R.drawable.flag_cote_divoire
        "ck" -> R.drawable.flag_cook_islands
        "cl" -> R.drawable.flag_chile
        "cm" -> R.drawable.flag_cameroon
        "cn" -> R.drawable.flag_china
        "co" -> R.drawable.flag_colombia
        "cr" -> R.drawable.flag_costa_rica
        "cu" -> R.drawable.flag_cuba
        "cv" -> R.drawable.flag_cape_verde
        "cw" -> R.drawable.flag_curacao
        "cx" -> R.drawable.flag_christmas_island
        "cy" -> R.drawable.flag_cyprus
        "cz" -> R.drawable.flag_czech_republic
        "de" -> R.drawable.flag_germany
        "dj" -> R.drawable.flag_djibouti
        "dk" -> R.drawable.flag_denmark
        "dm" -> R.drawable.flag_dominica
        "do" -> R.drawable.flag_dominican_republic
        "dz" -> R.drawable.flag_algeria
        "ec" -> R.drawable.flag_ecuador
        "ee" -> R.drawable.flag_estonia
        "eg" -> R.drawable.flag_egypt
        "er" -> R.drawable.flag_eritrea
        "es" -> R.drawable.flag_spain
        "et" -> R.drawable.flag_ethiopia
        "fi" -> R.drawable.flag_finland
        "fj" -> R.drawable.flag_fiji
        "fk" -> R.drawable.flag_falkland_islands
        "fm" -> R.drawable.flag_micronesia
        "fo" -> R.drawable.flag_faroe_islands
        "fr" -> R.drawable.flag_france
        "ga" -> R.drawable.flag_gabon
        "gb" -> R.drawable.flag_united_kingdom
        "gd" -> R.drawable.flag_grenada
        "ge" -> R.drawable.flag_georgia
        "gf" -> R.drawable.flag_guyane
        "gg" -> R.drawable.flag_guernsey
        "gh" -> R.drawable.flag_ghana
        "gi" -> R.drawable.flag_gibraltar
        "gl" -> R.drawable.flag_greenland
        "gm" -> R.drawable.flag_gambia
        "gn" -> R.drawable.flag_guinea
        "gp" -> R.drawable.flag_guadeloupe
        "gq" -> R.drawable.flag_equatorial_guinea
        "gr" -> R.drawable.flag_greece
        "gt" -> R.drawable.flag_guatemala
        "gu" -> R.drawable.flag_guam
        "gw" -> R.drawable.flag_guinea_bissau
        "gy" -> R.drawable.flag_guyana
        "hk" -> R.drawable.flag_hong_kong
        "hn" -> R.drawable.flag_honduras
        "hr" -> R.drawable.flag_croatia
        "ht" -> R.drawable.flag_haiti
        "hu" -> R.drawable.flag_hungary
        "id" -> R.drawable.flag_indonesia
        "ie" -> R.drawable.flag_ireland
        "il" -> R.drawable.flag_israel
        "im" -> R.drawable.flag_isleof_man // custom
        "is" -> R.drawable.flag_iceland
        "in" -> R.drawable.flag_india
        "io" -> R.drawable.flag_british_indian_ocean_territory
        "iq" -> R.drawable.flag_iraq_new
        "ir" -> R.drawable.flag_iran
        "it" -> R.drawable.flag_italy
        "je" -> R.drawable.flag_jersey
        "jm" -> R.drawable.flag_jamaica
        "jo" -> R.drawable.flag_jordan
        "jp" -> R.drawable.flag_japan
        "ke" -> R.drawable.flag_kenya
        "kg" -> R.drawable.flag_kyrgyzstan
        "kh" -> R.drawable.flag_cambodia
        "ki" -> R.drawable.flag_kiribati
        "km" -> R.drawable.flag_comoros
        "kn" -> R.drawable.flag_saint_kitts_and_nevis
        "kp" -> R.drawable.flag_north_korea
        "kr" -> R.drawable.flag_south_korea
        "kw" -> R.drawable.flag_kuwait
        "ky" -> R.drawable.flag_cayman_islands
        "kz" -> R.drawable.flag_kazakhstan
        "la" -> R.drawable.flag_laos
        "lb" -> R.drawable.flag_lebanon
        "lc" -> R.drawable.flag_saint_lucia
        "li" -> R.drawable.flag_liechtenstein
        "lk" -> R.drawable.flag_sri_lanka
        "lr" -> R.drawable.flag_liberia
        "ls" -> R.drawable.flag_lesotho
        "lt" -> R.drawable.flag_lithuania
        "lu" -> R.drawable.flag_luxembourg
        "lv" -> R.drawable.flag_latvia
        "ly" -> R.drawable.flag_libya
        "ma" -> R.drawable.flag_morocco
        "mc" -> R.drawable.flag_monaco
        "md" -> R.drawable.flag_moldova
        "me" -> R.drawable.flag_of_montenegro // custom
        "mf" -> R.drawable.flag_saint_martin
        "mg" -> R.drawable.flag_madagascar
        "mh" -> R.drawable.flag_marshall_islands
        "mk" -> R.drawable.flag_macedonia
        "ml" -> R.drawable.flag_mali
        "mm" -> R.drawable.flag_myanmar
        "mn" -> R.drawable.flag_mongolia
        "mo" -> R.drawable.flag_macao
        "mp" -> R.drawable.flag_northern_mariana_islands
        "mq" -> R.drawable.flag_martinique
        "mr" -> R.drawable.flag_mauritania
        "ms" -> R.drawable.flag_montserrat
        "mt" -> R.drawable.flag_malta
        "mu" -> R.drawable.flag_mauritius
        "mv" -> R.drawable.flag_maldives
        "mw" -> R.drawable.flag_malawi
        "mx" -> R.drawable.flag_mexico
        "my" -> R.drawable.flag_malaysia
        "mz" -> R.drawable.flag_mozambique
        "na" -> R.drawable.flag_namibia
        "nc" -> R.drawable.flag_new_caledonia // custom
        "ne" -> R.drawable.flag_niger
        "nf" -> R.drawable.flag_norfolk_island
        "ng" -> R.drawable.flag_nigeria
        "ni" -> R.drawable.flag_nicaragua
        "nl" -> R.drawable.flag_netherlands
        "no" -> R.drawable.flag_norway
        "np" -> R.drawable.flag_nepal
        "nr" -> R.drawable.flag_nauru
        "nu" -> R.drawable.flag_niue
        "nz" -> R.drawable.flag_new_zealand
        "om" -> R.drawable.flag_oman
        "pa" -> R.drawable.flag_panama
        "pe" -> R.drawable.flag_peru
        "pf" -> R.drawable.flag_french_polynesia
        "pg" -> R.drawable.flag_papua_new_guinea
        "ph" -> R.drawable.flag_philippines
        "pk" -> R.drawable.flag_pakistan
        "pl" -> R.drawable.flag_poland
        "pm" -> R.drawable.flag_saint_pierre
        "pn" -> R.drawable.flag_pitcairn_islands
        "pr" -> R.drawable.flag_puerto_rico
        "ps" -> R.drawable.flag_palestine
        "pt" -> R.drawable.flag_portugal
        "pw" -> R.drawable.flag_palau
        "py" -> R.drawable.flag_paraguay
        "qa" -> R.drawable.flag_qatar
        "re" -> R.drawable.flag_martinique // no exact flag found
        "ro" -> R.drawable.flag_romania
        "rs" -> R.drawable.flag_serbia // custom
        "ru" -> R.drawable.flag_russian_federation
        "rw" -> R.drawable.flag_rwanda
        "sa" -> R.drawable.flag_saudi_arabia
        "sb" -> R.drawable.flag_soloman_islands
        "sc" -> R.drawable.flag_seychelles
        "sd" -> R.drawable.flag_sudan
        "se" -> R.drawable.flag_sweden
        "sg" -> R.drawable.flag_singapore
        "sh" -> R.drawable.flag_saint_helena // custom
        "si" -> R.drawable.flag_slovenia
        "sk" -> R.drawable.flag_slovakia
        "sl" -> R.drawable.flag_sierra_leone
        "sm" -> R.drawable.flag_san_marino
        "sn" -> R.drawable.flag_senegal
        "so" -> R.drawable.flag_somalia
        "sr" -> R.drawable.flag_suriname
        "ss" -> R.drawable.flag_south_sudan
        "st" -> R.drawable.flag_sao_tome_and_principe
        "sv" -> R.drawable.flag_el_salvador
        "sx" -> R.drawable.flag_sint_maarten
        "sy" -> R.drawable.flag_syria
        "sz" -> R.drawable.flag_swaziland
        "tc" -> R.drawable.flag_turks_and_caicos_islands
        "td" -> R.drawable.flag_chad
        "tg" -> R.drawable.flag_togo
        "th" -> R.drawable.flag_thailand
        "tj" -> R.drawable.flag_tajikistan
        "tk" -> R.drawable.flag_tokelau // custom
        "tl" -> R.drawable.flag_timor_leste
        "tm" -> R.drawable.flag_turkmenistan
        "tn" -> R.drawable.flag_tunisia
        "to" -> R.drawable.flag_tonga
        "tr" -> R.drawable.flag_turkey
        "tt" -> R.drawable.flag_trinidad_and_tobago
        "tv" -> R.drawable.flag_tuvalu
        "tw" -> R.drawable.flag_taiwan
        "tz" -> R.drawable.flag_tanzania
        "ua" -> R.drawable.flag_ukraine
        "ug" -> R.drawable.flag_uganda
        "us" -> R.drawable.flag_united_states_of_america
        "uy" -> R.drawable.flag_uruguay
        "uz" -> R.drawable.flag_uzbekistan
        "va" -> R.drawable.flag_vatican_city
        "vc" -> R.drawable.flag_saint_vicent_and_the_grenadines
        "ve" -> R.drawable.flag_venezuela
        "vg" -> R.drawable.flag_british_virgin_islands
        "vi" -> R.drawable.flag_us_virgin_islands
        "vn" -> R.drawable.flag_vietnam
        "vu" -> R.drawable.flag_vanuatu
        "wf" -> R.drawable.flag_wallis_and_futuna
        "ws" -> R.drawable.flag_samoa
        "xk" -> R.drawable.flag_kosovo
        "ye" -> R.drawable.flag_yemen
        "yt" -> R.drawable.flag_martinique // no exact flag found
        "za" -> R.drawable.flag_south_africa
        "zm" -> R.drawable.flag_zambia
        "zw" -> R.drawable.flag_zimbabwe
        else -> R.drawable.flag_transparent
    }

}

fun getCountryCode(): ArrayList<Pair<String, String>>? {
    val countries: ArrayList<Pair<String, String>> = arrayListOf()
    countries.add(Pair("ad", "376"))
    countries.add(Pair("ae", "971"))
    countries.add(Pair("af", "93"))
    countries.add(Pair("ag", "1"))
    countries.add(Pair("ai", "1"))
    countries.add(Pair("al", "355"))
    countries.add(Pair("am", "374"))
    countries.add(Pair("ao", "244"))
    countries.add(Pair("aq", "672"))
    countries.add(Pair("ar", "54"))
    countries.add(Pair("as", "1"))
    countries.add(Pair("at", "43"))
    countries.add(Pair("au", "61"))
    countries.add(Pair("aw", "297"))
    countries.add(Pair("ax", "358"))
    countries.add(Pair("az", "994"))
    countries.add(Pair("ba", "387"))
    countries.add(Pair("bb", "1"))
    countries.add(Pair("bd", "880"))
    countries.add(Pair("be", "32"))
    countries.add(Pair("bf", "226"))
    countries.add(Pair("bg", "359"))
    countries.add(Pair("bh", "973"))
    countries.add(Pair("bi", "257"))
    countries.add(Pair("bj", "229"))
    countries.add(Pair("bl", "590"))
    countries.add(Pair("bm", "1"))
    countries.add(Pair("bn", "673"))
    countries.add(Pair("bo", "591"))
    countries.add(Pair("br", "55"))
    countries.add(Pair("bs", "1"))
    countries.add(Pair("bt", "975"))
    countries.add(Pair("bw", "267"))
    countries.add(Pair("by", "375"))
    countries.add(Pair("bz", "501"))
    countries.add(Pair("ca", "1"))
    countries.add(Pair("cc", "61"))
    countries.add(Pair("cd", "243"))
    countries.add(Pair("cf", "236"))
    countries.add(Pair("cg", "242"))
    countries.add(Pair("ch", "41"))
    countries.add(Pair("ci", "225"))
    countries.add(Pair("ck", "682"))
    countries.add(Pair("cl", "56"))
    countries.add(Pair("cm", "237"))
    countries.add(Pair("cn", "86"))
    countries.add(Pair("co", "57"))
    countries.add(Pair("cr", "506"))
    countries.add(Pair("cu", "53"))
    countries.add(Pair("cv", "238"))
    countries.add(Pair("cw", "599"))
    countries.add(Pair("cx", "61"))
    countries.add(Pair("cy", "357"))
    countries.add(Pair("cz", "420"))
    countries.add(Pair("de", "49"))
    countries.add(Pair("dj", "253"))
    countries.add(Pair("dk", "45"))
    countries.add(Pair("dm", "1"))
    countries.add(Pair("do", "1"))
    countries.add(Pair("dz", "213"))
    countries.add(Pair("ec", "593"))
    countries.add(Pair("ee", "372"))
    countries.add(Pair("eg", "20"))
    countries.add(Pair("er", "291"))
    countries.add(Pair("es", "34"))
    countries.add(Pair("et", "251"))
    countries.add(Pair("fi", "358"))
    countries.add(Pair("fj", "679"))
    countries.add(Pair("fk", "500"))
    countries.add(Pair("fm", "691"))
    countries.add(Pair("fo", "298"))
    countries.add(Pair("fr", "33"))
    countries.add(Pair("ga", "241"))
    countries.add(Pair("gb", "44"))
    countries.add(Pair("gd", "1"))
    countries.add(Pair("ge", "995"))
    countries.add(Pair("gf", "594"))
    countries.add(Pair("gh", "233"))
    countries.add(Pair("gi", "350"))
    countries.add(Pair("gl", "299"))
    countries.add(Pair("gm", "220"))
    countries.add(Pair("gn", "224"))
    countries.add(Pair("gp", "450"))
    countries.add(Pair("gq", "240"))
    countries.add(Pair("gr", "30"))
    countries.add(Pair("gt", "502"))
    countries.add(Pair("gu", "1"))
    countries.add(Pair("gw", "245"))
    countries.add(Pair("gy", "592"))
    countries.add(Pair("hk", "852"))
    countries.add(Pair("hn", "504"))
    countries.add(Pair("hr", "385"))
    countries.add(Pair("ht", "509"))
    countries.add(Pair("hu", "36"))
    countries.add(Pair("id", "62"))
    countries.add(Pair("ie", "353"))
    countries.add(Pair("il", "972"))
    countries.add(Pair("im", "44"))
    countries.add(Pair("is", "354"))
    countries.add(Pair("in", "91"))
    countries.add(Pair("io", "246"))
    countries.add(Pair("iq", "964"))
    countries.add(Pair("ir", "98"))
    countries.add(Pair("it", "39"))
    countries.add(Pair("je", "44"))
    countries.add(Pair("jm", "1"))
    countries.add(Pair("jo", "962"))
    countries.add(Pair("jp", "81"))
    countries.add(Pair("ke", "254"))
    countries.add(Pair("kg", "996"))
    countries.add(Pair("kh", "855"))
    countries.add(Pair("ki", "686"))
    countries.add(Pair("km", "269"))
    countries.add(Pair("kn", "1"))
    countries.add(Pair("kp", "850"))
    countries.add(Pair("kr", "82"))
    countries.add(Pair("kw", "965"))
    countries.add(Pair("ky", "1"))
    countries.add(Pair("kz", "7"))
    countries.add(Pair("la", "856"))
    countries.add(Pair("lb", "961"))
    countries.add(Pair("lc", "1"))
    countries.add(Pair("li", "423"))
    countries.add(Pair("lk", "94"))
    countries.add(Pair("lr", "231"))
    countries.add(Pair("ls", "266"))
    countries.add(Pair("lt", "370"))
    countries.add(Pair("lu", "352"))
    countries.add(Pair("lv", "371"))
    countries.add(Pair("ly", "218"))
    countries.add(Pair("ma", "212"))
    countries.add(Pair("mc", "377"))
    countries.add(Pair("md", "373"))
    countries.add(Pair("me", "382"))
    countries.add(Pair("mf", "590"))
    countries.add(Pair("mg", "261"))
    countries.add(Pair("mh", "692"))
    countries.add(Pair("mk", "389"))
    countries.add(Pair("ml", "223"))
    countries.add(Pair("mm", "95"))
    countries.add(Pair("mn", "976"))
    countries.add(Pair("mo", "853"))
    countries.add(Pair("mp", "1"))
    countries.add(Pair("mq", "596"))
    countries.add(Pair("mr", "222"))
    countries.add(Pair("ms", "1"))
    countries.add(Pair("mt", "356"))
    countries.add(Pair("mu", "230"))
    countries.add(Pair("mv", "960"))
    countries.add(Pair("mw", "265"))
    countries.add(Pair("mx", "52"))
    countries.add(Pair("my", "60"))
    countries.add(Pair("mz", "258"))
    countries.add(Pair("na", "264"))
    countries.add(Pair("nc", "687"))
    countries.add(Pair("ne", "227"))
    countries.add(Pair("nf", "672"))
    countries.add(Pair("ng", "234"))
    countries.add(Pair("ni", "505"))
    countries.add(Pair("nl", "31"))
    countries.add(Pair("no", "47"))
    countries.add(Pair("np", "977"))
    countries.add(Pair("nr", "674"))
    countries.add(Pair("nu", "683"))
    countries.add(Pair("nz", "64"))
    countries.add(Pair("om", "968"))
    countries.add(Pair("pa", "507"))
    countries.add(Pair("pe", "51"))
    countries.add(Pair("pf", "689"))
    countries.add(Pair("pg", "675"))
    countries.add(Pair("ph", "63"))
    countries.add(Pair("pk", "92"))
    countries.add(Pair("pl", "48"))
    countries.add(Pair("pm", "508"))
    countries.add(Pair("pn", "870"))
    countries.add(Pair("pr", "1"))
    countries.add(Pair("ps", "970"))
    countries.add(Pair("pt", "351"))
    countries.add(Pair("pw", "680"))
    countries.add(Pair("py", "595"))
    countries.add(Pair("qa", "974"))
    countries.add(Pair("re", "262"))
    countries.add(Pair("ro", "40"))
    countries.add(Pair("rs", "381"))
    countries.add(Pair("ru", "7"))
    countries.add(Pair("rw", "250"))
    countries.add(Pair("sa", "966"))
    countries.add(Pair("sb", "677"))
    countries.add(Pair("sc", "248"))
    countries.add(Pair("sd", "249"))
    countries.add(Pair("se", "46"))
    countries.add(Pair("sg", "65"))
    countries.add(Pair("sh", "290"))
    countries.add(Pair("si", "386"))
    countries.add(Pair("sk", "421"))
    countries.add(Pair("sl", "232"))
    countries.add(Pair("sm", "378"))
    countries.add(Pair("sn", "221"))
    countries.add(Pair("so", "252"))
    countries.add(Pair("sr", "597"))
    countries.add(Pair("ss", "211"))
    countries.add(Pair("st", "239"))
    countries.add(Pair("sv", "503"))
    countries.add(Pair("sx", "1"))
    countries.add(Pair("sy", "963"))
    countries.add(Pair("sz", "268"))
    countries.add(Pair("tc", "1"))
    countries.add(Pair("td", "235"))
    countries.add(Pair("tg", "228"))
    countries.add(Pair("th", "66"))
    countries.add(Pair("tj", "992"))
    countries.add(Pair("tk", "690"))
    countries.add(Pair("tl", "670"))
    countries.add(Pair("tm", "993"))
    countries.add(Pair("tn", "216"))
    countries.add(Pair("to", "676"))
    countries.add(Pair("tr", "90"))
    countries.add(Pair("tt", "1"))
    countries.add(Pair("tv", "688"))
    countries.add(Pair("tw", "886"))
    countries.add(Pair("tz", "255"))
    countries.add(Pair("ua", "380"))
    countries.add(Pair("ug", "256"))
    countries.add(Pair("us", "1"))
    countries.add(Pair("uy", "598"))
    countries.add(Pair("uz", "998"))
    countries.add(Pair("va", "379"))
    countries.add(Pair("vc", "1"))
    countries.add(Pair("ve", "58"))
    countries.add(Pair("vg", "1"))
    countries.add(Pair("vi", "1"))
    countries.add(Pair("vn", "84"))
    countries.add(Pair("vu", "678"))
    countries.add(Pair("wf", "681"))
    countries.add(Pair("ws", "685"))
    countries.add(Pair("xk", "383"))
    countries.add(Pair("ye", "967"))
    countries.add(Pair("yt", "262"))
    countries.add(Pair("za", "27"))
    countries.add(Pair("zm", "260"))
    countries.add(Pair("zw", "263"))
    return countries
}
