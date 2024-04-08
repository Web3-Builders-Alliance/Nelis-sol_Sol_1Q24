package spl.cards.program

val splCardProgramJson = """
    {
      "version": "0.1.0",
      "name": "splcards",
      "instructions": [
        {
          "name": "newFullWalletPolicy",
          "accounts": [
            {
              "name": "payer",
              "isMut": true,
              "isSigner": true
            },
            {
              "name": "walletPolicy",
              "isMut": true,
              "isSigner": false
            },
            {
              "name": "systemProgram",
              "isMut": false,
              "isSigner": false
            }
          ],
          "args": [
            {
              "name": "signer1",
              "type": {
                "option": "publicKey"
              }
            },
            {
              "name": "requiredSigner1",
              "type": "bool"
            },
            {
              "name": "signer2",
              "type": {
                "option": "publicKey"
              }
            },
            {
              "name": "requiredSigner2",
              "type": "bool"
            },
            {
              "name": "allowList",
              "type": {
                "vec": "publicKey"
              }
            },
            {
              "name": "blockList",
              "type": {
                "vec": "publicKey"
              }
            },
            {
              "name": "spendingWindow",
              "type": {
                "option": {
                  "array": [
                    "i64",
                    2
                  ]
                }
              }
            }
          ]
        },
        {
          "name": "newWalletPolicy",
          "accounts": [
            {
              "name": "payer",
              "isMut": true,
              "isSigner": true
            },
            {
              "name": "walletPolicy",
              "isMut": true,
              "isSigner": false
            },
            {
              "name": "systemProgram",
              "isMut": false,
              "isSigner": false
            }
          ],
          "args": []
        },
        {
          "name": "addSigner1ToWalletPolicy",
          "accounts": [
            {
              "name": "payer",
              "isMut": true,
              "isSigner": true
            },
            {
              "name": "walletPolicy",
              "isMut": true,
              "isSigner": false
            },
            {
              "name": "systemProgram",
              "isMut": false,
              "isSigner": false
            }
          ],
          "args": [
            {
              "name": "signer1",
              "type": "publicKey"
            },
            {
              "name": "required",
              "type": "bool"
            }
          ]
        },
        {
          "name": "removeSigner1FromWalletPolicy",
          "accounts": [
            {
              "name": "payer",
              "isMut": true,
              "isSigner": true
            },
            {
              "name": "walletPolicy",
              "isMut": true,
              "isSigner": false
            },
            {
              "name": "systemProgram",
              "isMut": false,
              "isSigner": false
            }
          ],
          "args": []
        },
        {
          "name": "addSigner2ToWalletPolicy",
          "accounts": [
            {
              "name": "payer",
              "isMut": true,
              "isSigner": true
            },
            {
              "name": "walletPolicy",
              "isMut": true,
              "isSigner": false
            },
            {
              "name": "systemProgram",
              "isMut": false,
              "isSigner": false
            }
          ],
          "args": [
            {
              "name": "signer2",
              "type": "publicKey"
            },
            {
              "name": "required",
              "type": "bool"
            }
          ]
        },
        {
          "name": "removeSigner2FromWalletPolicy",
          "accounts": [
            {
              "name": "payer",
              "isMut": true,
              "isSigner": true
            },
            {
              "name": "walletPolicy",
              "isMut": true,
              "isSigner": false
            },
            {
              "name": "systemProgram",
              "isMut": false,
              "isSigner": false
            }
          ],
          "args": []
        },
        {
          "name": "addAllowedPublickeysToWalletPolicy",
          "accounts": [
            {
              "name": "payer",
              "isMut": true,
              "isSigner": true
            },
            {
              "name": "walletPolicy",
              "isMut": true,
              "isSigner": false
            },
            {
              "name": "systemProgram",
              "isMut": false,
              "isSigner": false
            }
          ],
          "args": [
            {
              "name": "allowedPubkeyList",
              "type": {
                "vec": "publicKey"
              }
            }
          ]
        },
        {
          "name": "removeAllowedPublickeysFromWalletPolicy",
          "accounts": [
            {
              "name": "payer",
              "isMut": true,
              "isSigner": true
            },
            {
              "name": "walletPolicy",
              "isMut": true,
              "isSigner": false
            },
            {
              "name": "systemProgram",
              "isMut": false,
              "isSigner": false
            }
          ],
          "args": [
            {
              "name": "removePubkeyList",
              "type": {
                "vec": "publicKey"
              }
            }
          ]
        },
        {
          "name": "addBlockedPublickeysToWalletPolicy",
          "accounts": [
            {
              "name": "payer",
              "isMut": true,
              "isSigner": true
            },
            {
              "name": "walletPolicy",
              "isMut": true,
              "isSigner": false
            },
            {
              "name": "systemProgram",
              "isMut": false,
              "isSigner": false
            }
          ],
          "args": [
            {
              "name": "blockedPubkeyList",
              "type": {
                "vec": "publicKey"
              }
            }
          ]
        },
        {
          "name": "removeBlockedPublickeysFromWalletPolicy",
          "accounts": [
            {
              "name": "payer",
              "isMut": true,
              "isSigner": true
            },
            {
              "name": "walletPolicy",
              "isMut": true,
              "isSigner": false
            },
            {
              "name": "systemProgram",
              "isMut": false,
              "isSigner": false
            }
          ],
          "args": [
            {
              "name": "removePubkeyList",
              "type": {
                "vec": "publicKey"
              }
            }
          ]
        },
        {
          "name": "addSpendingWindowToWalletPolicy",
          "accounts": [
            {
              "name": "payer",
              "isMut": true,
              "isSigner": true
            },
            {
              "name": "walletPolicy",
              "isMut": true,
              "isSigner": false
            },
            {
              "name": "systemProgram",
              "isMut": false,
              "isSigner": false
            }
          ],
          "args": [
            {
              "name": "spendingWindow",
              "type": {
                "array": [
                  "i64",
                  2
                ]
              }
            }
          ]
        },
        {
          "name": "removeSpendingWindowFromWalletPolicy",
          "accounts": [
            {
              "name": "payer",
              "isMut": true,
              "isSigner": true
            },
            {
              "name": "walletPolicy",
              "isMut": true,
              "isSigner": false
            },
            {
              "name": "systemProgram",
              "isMut": false,
              "isSigner": false
            }
          ],
          "args": []
        },
        {
          "name": "deleteWalletPolicy",
          "accounts": [
            {
              "name": "payer",
              "isMut": true,
              "isSigner": true
            },
            {
              "name": "walletPolicy",
              "isMut": true,
              "isSigner": false
            },
            {
              "name": "systemProgram",
              "isMut": false,
              "isSigner": false
            }
          ],
          "args": []
        },
        {
          "name": "newFullTokenPolicy",
          "accounts": [
            {
              "name": "payer",
              "isMut": true,
              "isSigner": true
            },
            {
              "name": "mintWrapped",
              "isMut": false,
              "isSigner": false
            },
            {
              "name": "tokenPolicy",
              "isMut": true,
              "isSigner": false
            },
            {
              "name": "systemProgram",
              "isMut": false,
              "isSigner": false
            }
          ],
          "args": [
            {
              "name": "spendLimitAmount",
              "type": {
                "option": "u64"
              }
            }
          ]
        },
        {
          "name": "newTokenPolicy",
          "accounts": [
            {
              "name": "payer",
              "isMut": true,
              "isSigner": true
            },
            {
              "name": "mintWrapped",
              "isMut": false,
              "isSigner": false
            },
            {
              "name": "tokenPolicy",
              "isMut": true,
              "isSigner": false
            },
            {
              "name": "systemProgram",
              "isMut": false,
              "isSigner": false
            }
          ],
          "args": []
        },
        {
          "name": "addSpendLimitToTokenPolicy",
          "accounts": [
            {
              "name": "payer",
              "isMut": true,
              "isSigner": true
            },
            {
              "name": "mintWrapped",
              "isMut": false,
              "isSigner": false
            },
            {
              "name": "tokenPolicy",
              "isMut": true,
              "isSigner": false
            },
            {
              "name": "systemProgram",
              "isMut": false,
              "isSigner": false
            }
          ],
          "args": [
            {
              "name": "amount",
              "type": "u64"
            }
          ]
        },
        {
          "name": "removeSpendLimitFromTokenPolicy",
          "accounts": [
            {
              "name": "payer",
              "isMut": true,
              "isSigner": true
            },
            {
              "name": "mintWrapped",
              "isMut": false,
              "isSigner": false
            },
            {
              "name": "tokenPolicy",
              "isMut": true,
              "isSigner": false
            },
            {
              "name": "systemProgram",
              "isMut": false,
              "isSigner": false
            }
          ],
          "args": []
        },
        {
          "name": "deleteTokenPolicy",
          "accounts": [
            {
              "name": "payer",
              "isMut": true,
              "isSigner": true
            },
            {
              "name": "mintWrapped",
              "isMut": false,
              "isSigner": false
            },
            {
              "name": "tokenPolicy",
              "isMut": true,
              "isSigner": false
            },
            {
              "name": "systemProgram",
              "isMut": false,
              "isSigner": false
            }
          ],
          "args": []
        },
        {
          "name": "newWrapper",
          "accounts": [
            {
              "name": "payer",
              "isMut": true,
              "isSigner": true
            },
            {
              "name": "mintWrapped",
              "isMut": true,
              "isSigner": true
            },
            {
              "name": "mintOriginal",
              "isMut": true,
              "isSigner": false
            },
            {
              "name": "wrapper",
              "isMut": true,
              "isSigner": false
            },
            {
              "name": "vault",
              "isMut": true,
              "isSigner": false
            },
            {
              "name": "extraAccountMetaList",
              "isMut": true,
              "isSigner": false
            },
            {
              "name": "rent",
              "isMut": false,
              "isSigner": false
            },
            {
              "name": "associatedTokenProgram",
              "isMut": false,
              "isSigner": false
            },
            {
              "name": "tokenProgram",
              "isMut": false,
              "isSigner": false
            },
            {
              "name": "systemProgram",
              "isMut": false,
              "isSigner": false
            }
          ],
          "args": [
            {
              "name": "name",
              "type": "string"
            },
            {
              "name": "symbol",
              "type": "string"
            },
            {
              "name": "uri",
              "type": "string"
            }
          ]
        },
        {
          "name": "updateWrapper",
          "accounts": [
            {
              "name": "payer",
              "isMut": true,
              "isSigner": true
            },
            {
              "name": "mintWrapped",
              "isMut": true,
              "isSigner": true
            },
            {
              "name": "mintOriginal",
              "isMut": true,
              "isSigner": false
            },
            {
              "name": "wrapper",
              "isMut": true,
              "isSigner": false
            },
            {
              "name": "associatedTokenProgram",
              "isMut": false,
              "isSigner": false
            },
            {
              "name": "tokenProgram",
              "isMut": false,
              "isSigner": false
            },
            {
              "name": "systemProgram",
              "isMut": false,
              "isSigner": false
            }
          ],
          "args": [
            {
              "name": "symbol",
              "type": "string"
            }
          ]
        },
        {
          "name": "wrapperClose",
          "accounts": [
            {
              "name": "payer",
              "isMut": true,
              "isSigner": true
            },
            {
              "name": "mintWrapped",
              "isMut": true,
              "isSigner": true
            },
            {
              "name": "mintOriginal",
              "isMut": true,
              "isSigner": false
            },
            {
              "name": "wrapper",
              "isMut": true,
              "isSigner": false
            },
            {
              "name": "associatedTokenProgram",
              "isMut": false,
              "isSigner": false
            },
            {
              "name": "tokenProgram",
              "isMut": false,
              "isSigner": false
            },
            {
              "name": "systemProgram",
              "isMut": false,
              "isSigner": false
            }
          ],
          "args": []
        },
        {
          "name": "wrap",
          "accounts": [
            {
              "name": "payer",
              "isMut": true,
              "isSigner": true
            },
            {
              "name": "payerAtaOriginal",
              "isMut": true,
              "isSigner": false
            },
            {
              "name": "payerAtaWrapped",
              "isMut": true,
              "isSigner": false
            },
            {
              "name": "mintOriginal",
              "isMut": false,
              "isSigner": false
            },
            {
              "name": "mintWrapped",
              "isMut": true,
              "isSigner": false
            },
            {
              "name": "wrapper",
              "isMut": false,
              "isSigner": false
            },
            {
              "name": "vault",
              "isMut": true,
              "isSigner": false
            },
            {
              "name": "associatedTokenProgram",
              "isMut": false,
              "isSigner": false
            },
            {
              "name": "tokenProgram",
              "isMut": false,
              "isSigner": false
            },
            {
              "name": "tokenExtensionsProgram",
              "isMut": false,
              "isSigner": false
            },
            {
              "name": "systemProgram",
              "isMut": false,
              "isSigner": false
            }
          ],
          "args": [
            {
              "name": "amount",
              "type": "u64"
            }
          ]
        },
        {
          "name": "unwrap",
          "accounts": [
            {
              "name": "payer",
              "isMut": true,
              "isSigner": true
            },
            {
              "name": "payerAtaOriginal",
              "isMut": true,
              "isSigner": false
            },
            {
              "name": "payerAtaWrapped",
              "isMut": true,
              "isSigner": false
            },
            {
              "name": "mintOriginal",
              "isMut": false,
              "isSigner": false
            },
            {
              "name": "mintWrapped",
              "isMut": false,
              "isSigner": false
            },
            {
              "name": "wrapper",
              "isMut": false,
              "isSigner": false
            },
            {
              "name": "vault",
              "isMut": true,
              "isSigner": false
            },
            {
              "name": "associatedTokenProgram",
              "isMut": false,
              "isSigner": false
            },
            {
              "name": "tokenProgram",
              "isMut": false,
              "isSigner": false
            },
            {
              "name": "tokenExtensionsProgram",
              "isMut": false,
              "isSigner": false
            },
            {
              "name": "systemProgram",
              "isMut": false,
              "isSigner": false
            }
          ],
          "args": [
            {
              "name": "amount",
              "type": "u64"
            }
          ]
        },
        {
          "name": "transferHook",
          "accounts": [
            {
              "name": "sourceToken",
              "isMut": false,
              "isSigner": false
            },
            {
              "name": "mint",
              "isMut": false,
              "isSigner": false
            },
            {
              "name": "destinationToken",
              "isMut": false,
              "isSigner": false
            },
            {
              "name": "owner",
              "isMut": false,
              "isSigner": false
            },
            {
              "name": "extraAccountMetaList",
              "isMut": false,
              "isSigner": false
            },
            {
              "name": "walletPolicy",
              "isMut": true,
              "isSigner": false
            },
            {
              "name": "tokenPolicy",
              "isMut": true,
              "isSigner": false
            }
          ],
          "args": [
            {
              "name": "amount",
              "type": "u64"
            }
          ]
        }
      ],
      "accounts": [
        {
          "name": "TokenPolicyState",
          "type": {
            "kind": "struct",
            "fields": [
              {
                "name": "authority",
                "type": "publicKey"
              },
              {
                "name": "mint",
                "type": "publicKey"
              },
              {
                "name": "spentLast24",
                "type": {
                  "array": [
                    "i64",
                    2
                  ]
                }
              },
              {
                "name": "spendLimitAmount",
                "type": {
                  "option": "u64"
                }
              },
              {
                "name": "bump",
                "type": "u8"
              }
            ]
          }
        },
        {
          "name": "WalletPolicyState",
          "type": {
            "kind": "struct",
            "fields": [
              {
                "name": "authority",
                "type": "publicKey"
              },
              {
                "name": "signer1",
                "type": {
                  "option": "publicKey"
                }
              },
              {
                "name": "requiredSigner1",
                "type": "bool"
              },
              {
                "name": "signer2",
                "type": {
                  "option": "publicKey"
                }
              },
              {
                "name": "requiredSigner2",
                "type": "bool"
              },
              {
                "name": "allowList",
                "type": {
                  "vec": "publicKey"
                }
              },
              {
                "name": "blockList",
                "type": {
                  "vec": "publicKey"
                }
              },
              {
                "name": "spendingWindow",
                "type": {
                  "option": {
                    "array": [
                      "i64",
                      2
                    ]
                  }
                }
              },
              {
                "name": "bump",
                "type": "u8"
              }
            ]
          }
        },
        {
          "name": "WrapperState",
          "type": {
            "kind": "struct",
            "fields": [
              {
                "name": "symbol",
                "type": "string"
              },
              {
                "name": "mintOriginal",
                "type": "publicKey"
              },
              {
                "name": "mintWrapped",
                "type": "publicKey"
              },
              {
                "name": "vault",
                "type": "publicKey"
              },
              {
                "name": "bump",
                "type": "u8"
              }
            ]
          }
        }
      ],
      "types": [],
      "errors": [
        {
          "code": 6000,
          "name": "SpendLimitExceeded",
          "msg": "Spend Limit Exceeded"
        },
        {
          "code": 6001,
          "name": "PubkeyNotInAllowList",
          "msg": "Pubkey Not In Allow List"
        },
        {
          "code": 6002,
          "name": "PubkeyInBlockList",
          "msg": "Pubkey In Block List"
        },
        {
          "code": 6003,
          "name": "SpendingWindowViolation",
          "msg": "Spending Window Violation"
        },
        {
          "code": 6004,
          "name": "MissingRequiredSigners",
          "msg": "Missing Required Signers"
        },
        {
          "code": 6005,
          "name": "NotInSpendWindow",
          "msg": "Not In Spend Window"
        },
        {
          "code": 6006,
          "name": "TransferHookIntentionalFail",
          "msg": "Transfer Hook fail - for testing"
        }
      ],
    "metadata": {
      "address": "6jPXVk78mLJq3MAz24gasxmYmV2f3bYDd5Rp5zK92tew",
      "origin": "anchor"
    }
    }
""".trimIndent()