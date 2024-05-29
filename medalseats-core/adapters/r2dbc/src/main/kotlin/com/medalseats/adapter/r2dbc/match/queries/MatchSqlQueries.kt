package com.medalseats.adapter.r2dbc.match.queries

import com.medalseats.adapter.r2dbc.common.DefaultSqlQueries
import com.unicamp.medalseats.match.MatchId

object MatchSqlQueries : DefaultSqlQueries() {

    fun selectMatch() =
        """
            SELECT
                id,
                title,
                subtitle,
                description,
                date,
                latitude,
                longitude,
                banner_url,
                stadium_url
            FROM match
            WHERE 1 = 1
        """

    fun whereId(id: MatchId?) =
        id?.let {
            """
                AND id = :id
            """
        }

}
