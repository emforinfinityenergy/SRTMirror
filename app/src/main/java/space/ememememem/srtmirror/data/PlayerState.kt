package space.ememememem.srtmirror.data

sealed class PlayerState {
    data class Waiting(val ip: String, val port: Int) : PlayerState()
    data class Connecting(val ip: String, val port: Int) : PlayerState()
    object Playing : PlayerState()
    data class Error(val message: String) : PlayerState()
}
