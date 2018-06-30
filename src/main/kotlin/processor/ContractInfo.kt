package processor

data class ContractInfo(val contractClassName: String,
                        val tableName: String,
                        val columns: Set<Pair<String, String>>)
