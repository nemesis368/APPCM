package com.miraflores.agenda.interfaces

import com.miraflores.agenda.model.Client

interface ClientDao {
    fun insertClient(client: Client): Long
    fun getAllClients(): ArrayList<Client>
    fun deleteClient(id: Int): Boolean
}
