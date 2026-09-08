package br.com.bloqfone.services

import android.telecom.Call
import android.telecom.CallScreeningService
import android.util.Log
import br.com.bloqfone.data.ConfigRepository
import br.com.bloqfone.data.ContactsRepository

class CallInterceptorService : CallScreeningService() {

    private lateinit var contactsRepository: ContactsRepository
    private lateinit var configRepository: ConfigRepository

    override fun onCreate() {
        super.onCreate()
        // Initialize the repositories when the service is created
        contactsRepository = ContactsRepository(this)
        configRepository = ConfigRepository(this)
    }

    override fun onScreenCall(callDetails: Call.Details) {
        // Ignore outgoing calls, we only want to intercept incoming ones
        if (callDetails.callDirection != Call.Details.DIRECTION_INCOMING) return

        // Extract the phone number from the call details, defaulting to "Desconhecido" if it's null
        val phoneNumber = callDetails.handle?.schemeSpecificPart ?: "Desconhecido"
        Log.d("Interceptador", "Analisando chamada de: $phoneNumber")

        val responseBuilder = CallResponse.Builder()

        // Check if the number is considered spam based on our rules
        if (isSpam(phoneNumber)) {
            Log.d("Interceptador", "Bloqueado!")
            responseBuilder
                .setDisallowCall(true)
                .setRejectCall(true)
                .setSkipCallLog(false)
                .setSkipNotification(true)
        } else {
            responseBuilder.setDisallowCall(false)
                .setRejectCall(false)
        }

        respondToCall(callDetails, responseBuilder.build())
    }

    private fun isSpam(phoneNumber: String): Boolean {
        // If the number is unknown, we consider it spam
        if (phoneNumber == "Desconhecido") return true

       val isFocusModeEnabled= true

        if(isFocusModeEnabled){
            val isContact = ContactsRepository(this).doesNumberExistInContacts(phoneNumber)
            if(!isContact){
                Log.d("Interceptador", "Número não é contato: $phoneNumber")
                return true
            }
        }
        return phoneNumber.startsWith("0303") || phoneNumber.startsWith("+550303")


    }

}