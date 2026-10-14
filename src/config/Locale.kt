package cat.freya.alloy.config

@JvmInline
value class LocaleString1(val inner: String) {
    fun with(arg1: Any): String {
        return this.inner.replace("{1}", arg1.toString())
    }

    override fun toString(): String = "[LocaleString1]"
}

@JvmInline
value class LocaleString2(val inner: String) {
    fun with(arg1: Any, arg2: Any): String {
        return this.inner.replace("{1}", arg1.toString()).replace("{2}", arg2.toString())
    }

    override fun toString(): String = "[LocaleString2]"
}

@JvmInline
value class LocaleString3(val inner: String) {
    fun with(arg1: Any, arg2: Any, arg3: Any): String {
        return this.inner
            .replace("{1}", arg1.toString())
            .replace("{2}", arg2.toString())
            .replace("{3}", arg3.toString())
    }

    override fun toString(): String = "[LocaleString3]"
}
