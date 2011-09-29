/**
 * Copyright 2011 Kurtis L. Nusbaum
 * 
 * This file is part of UDJ.
 * 
 * UDJ is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version.
 * 
 * UDJ is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with UDJ.  If not, see <http://www.gnu.org/licenses/>.
 */

#ifndef CONFIG_DEFS_HPP
#define CONFIG_DEFS_HPP
#include <QUrl>

namespace UDJ{


typedef long libraryid_t;
typedef long playlistid_t;
typedef long partyid_t;
typedef long partierid_t;

} //end namespace

#ifdef UDJ_DEBUG_BUILD
#include <iostream>
#include <QSqlError>

#define EXEC_SQL( MESSAGE , STMT, QSQLOBJECT ) \
	if(!( STMT )){ \
		std::cerr << MESSAGE << std::endl; \
		std::cerr << "SQL ERROR MESSAGE: '" << QSQLOBJECT.lastError().text().toStdString() << "'" << std::endl; \
		std::cerr << std::endl; \
	} \

#define EXEC_INSERT( MESSAGE, QSQLOBJECT, RESULT_VAR) \
  QSQLOBJECT.exec(); \
  if( QSQLOBJECT.lastError() != QSqlError::NoError ){ \
    std::cerr << MESSAGE << std::endl; \
		std::cerr << "SQL ERROR MESSAGE: '" << \
    QSQLOBJECT.lastError().text().toStdString() << "'" << std::endl; \
		std::cerr << std::endl; \
  } \
  else{ \
    RESULT_VAR = QSQLOBJECT.lastInsertId().value<libraryid_t>(); \
  }

#else

#define EXEC_SQL( MESSAGE, STMT, QSQLOBJECT) \
	STMT;

#define EXEC_INSERT( MESSAGE, QSQLOBJECT, RESULT_VAR) \
  QSQLOBJECT.exec(); \
  RESULT_VAR = QSQLOBJECT.lastInsertId().value<libraryid_t>();

#endif


#endif //CONFIG_DEFS_HPP
