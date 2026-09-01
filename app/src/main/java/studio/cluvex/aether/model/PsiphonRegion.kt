package studio.cluvex.aether.model

/**
 * Supported Psiphon egress countries with flags, ISO codes, and localized display names.
 */
enum class PsiphonRegion(
    val code: String,
    val flag: String,
    val enName: String,
    val faName: String,
) {
    AUTO("AUTO", "⚡", "Fastest Location (Auto)", "سریع‌ترین (خودکار)"),
    DIRECT("DIRECT", "🌩️", "Direct Cloudflare WARP", "اتصال مستقیم وارپ"),
    US("US", "🇺🇸", "United States", "ایالات متحده"),
    DE("DE", "🇩🇪", "Germany", "آلمان"),
    GB("GB", "🇬🇧", "United Kingdom", "انگلستان"),
    NL("NL", "🇳🇱", "Netherlands", "هلند"),
    FR("FR", "🇫🇷", "France", "فرانسه"),
    CA("CA", "🇨🇦", "Canada", "کانادا"),
    JP("JP", "🇯🇵", "Japan", "ژاپن"),
    SG("SG", "🇸🇬", "Singapore", "سنگاپور"),
    CH("CH", "🇨🇭", "Switzerland", "سوئیس"),
    SE("SE", "🇸🇪", "Sweden", "سوئد"),
    IT("IT", "🇮🇹", "Italy", "ایتالیا"),
    ES("ES", "🇪🇸", "Spain", "اسپانیا"),
    TR("TR", "🇹🇷", "Turkey", "ترکیه"),
    PL("PL", "🇵🇱", "Poland", "لهستان"),
    AT("AT", "🇦🇹", "Austria", "اتریش"),
    BE("BE", "🇧🇪", "Belgium", "بلژیک"),
    NO("NO", "🇳🇴", "Norway", "نروژ"),
    FI("FI", "🇫🇮", "Finland", "فنلاند"),
    RO("RO", "🇷🇴", "Romania", "رومانی"),
    AU("AU", "🇦🇺", "Australia", "استرالیا"),
    IN("IN", "🇮🇳", "India", "هند"),
    BR("BR", "🇧🇷", "Brazil", "برزیل"),
    // New additions from xPsiphon
    BG("BG", "🇧🇬", "Bulgaria", "بلغارستان"),
    CZ("CZ", "🇨🇿", "Czechia", "جمهوری چک"),
    DK("DK", "🇩🇰", "Denmark", "دانمارک"),
    EE("EE", "🇪🇪", "Estonia", "استونی"),
    ID("ID", "🇮🇩", "Indonesia", "اندونزی"),
    IE("IE", "🇮🇪", "Ireland", "ایرلند"),
    LT("LT", "🇱🇹", "Lithuania", "لیتوانی"),
    RS("RS", "🇷🇸", "Serbia", "صربستان");

    companion object {
        fun fromCode(code: String?): PsiphonRegion {
            if (code.isNullOrBlank()) return AUTO
            return entries.firstOrNull { it.code.equals(code, ignoreCase = true) } ?: AUTO
        }
    }
}
